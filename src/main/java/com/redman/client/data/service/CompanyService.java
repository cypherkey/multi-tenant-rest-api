package com.redman.client.data.service;

import java.util.List;

import com.redman.client.data.dto.CompanyDTO;

public interface CompanyService {
	CompanyDTO create(CompanyDTO company);
	CompanyDTO findByName(String name);
	List<CompanyDTO> findAll();
}
