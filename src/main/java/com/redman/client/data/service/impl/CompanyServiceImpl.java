package com.redman.client.data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redman.client.data.dao.CompanyDao;
import com.redman.client.data.dto.CompanyDTO;
import com.redman.client.data.mapper.CompanyMapper;
import com.redman.client.data.model.CompanyEntity;
import com.redman.client.data.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {
	@Autowired
    private CompanyDao CompanyDao;
	
	public CompanyDTO create(CompanyDTO dto) {
		CompanyEntity entity = CompanyMapper.mapDTOIntoEntity(dto);
		entity = CompanyDao.saveAndFlush(entity);
		return CompanyMapper.mapEntityIntoDTO(entity);
	}
	
	public CompanyDTO findByName(String name) {
		return CompanyMapper.mapEntityIntoDTO(CompanyDao.findByName(name));
	}
	
	public List<CompanyDTO> findAll() {
		return CompanyMapper.mapEntitiesIntoDTOs(CompanyDao.findAll());
	}
}
