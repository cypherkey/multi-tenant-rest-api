package com.redman.client.data.mapper;

import com.redman.client.data.model.CompanyEntity;
import com.redman.client.data.dto.CompanyDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static java.util.stream.Collectors.toList;

public class CompanyMapper {
    public static List<CompanyDTO> mapEntitiesIntoDTOs(List<CompanyEntity> entities) {
        return entities.stream()
                .map(CompanyMapper::mapEntityIntoDTO)
                .collect(toList());
    }
    
    public static CompanyEntity mapDTOIntoEntity(CompanyDTO dto, CompanyEntity entity) {
    	entity.setName(dto.getName());
    	entity.setContactName(dto.getContactName());
    	entity.setContactEmail(dto.getContactEmail());
    	entity.setMaxAccounts(dto.getMaxAccounts());
    	entity.setMaxSize(dto.getMaxSize());
    	return entity;
    }

    public static CompanyEntity mapDTOIntoEntity(CompanyDTO dto) {
    	return mapDTOIntoEntity(dto, new CompanyEntity());
    }
    
    public static CompanyDTO mapEntityIntoDTO(CompanyEntity entity) {
    	// Model is null, no entry found in the database
    	if (entity == null) {
    		return null;
    	}
        
    	CompanyDTO dto = new CompanyDTO();
    	dto.setName(entity.getName());
    	// TODO: Set field level permission
    	dto.setContactName(entity.getContactName());
    	dto.setContactEmail(entity.getContactEmail());
    	dto.setMaxAccounts(entity.getMaxAccounts());
    	dto.setMaxSize(entity.getMaxSize());
        return dto;
    }

    public static Page<CompanyDTO> mapEntityPageIntoDTOPage(Pageable page, Page<CompanyEntity> source) {
        List<CompanyDTO> dtos = mapEntitiesIntoDTOs(source.getContent());
        return new PageImpl<>(dtos, page, source.getTotalElements());
    }
}
