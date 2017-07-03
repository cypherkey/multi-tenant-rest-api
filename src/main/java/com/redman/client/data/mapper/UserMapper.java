package com.redman.client.data.mapper;

import com.redman.client.data.model.UserEntity;
import com.redman.client.data.security.ConvertIntToLong;
import com.redman.client.data.security.ConvertLongToInt;
import com.redman.client.data.security.ModelConverter;
import com.redman.client.data.security.ModelDTOMapper;
import com.redman.client.data.dto.UserDTO;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static java.util.stream.Collectors.toList;

public class UserMapper {
	
	public static Logger LOGGER = LoggerFactory.getLogger(UserMapper.class);
	
    public static List<UserDTO> mapEntitiesIntoDTOs(List<UserEntity> entities) {
        return entities.stream()
                .map(UserMapper::mapEntityIntoDTO)
                .collect(toList());
    }
    
    public static UserEntity mapDTOIntoEntity(UserDTO dto, UserEntity entity) {
    	entity.setLogin(dto.getLogin());
    	entity.setPassword(dto.getPassword());
    	//entity.setQuota(dto.getQuota());
    	ModelDTOMapper.writeDtoFieldToEntity(dto, "quota", entity, "quota", new ConvertLongToInt());
    	entity.setEnabled(dto.getEnabled());
    	return entity;
    }

    public static UserEntity mapDTOIntoEntity(UserDTO dto) {
    	return mapDTOIntoEntity(dto, new UserEntity());
    }
    
    public static UserDTO mapEntityIntoDTO(UserEntity entity) {
    	// Model is null, no entry found in the database
    	if (entity == null) {
    		return null;
    	}
        
    	UserDTO dto = new UserDTO();
    	
    	dto.setLogin(entity.getLogin());

    	dto.setPassword(entity.getPassword());
    	// mapValue(entity, dto, "password");

    	//dto.setQuota(entity.getQuota());
    	ModelDTOMapper.readEntityFieldToDto(dto, "quota", entity, "quota", new ConvertIntToLong());
    	
    	// dto.setEnabled(entity.getEnabled());
    	ModelDTOMapper.readEntityFieldToDto(dto, entity, "enabled");
        return dto;
    }

    public static Page<UserDTO> mapEntityPageIntoDTOPage(Pageable page, Page<UserEntity> source) {
        List<UserDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, page, source.getTotalElements());
    }
}

