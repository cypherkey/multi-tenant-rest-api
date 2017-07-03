package com.redman.client.data.service;

import com.redman.client.data.dto.CompanyDTO;

public interface CompanyService {
	CompanyDTO create(CompanyDTO company);
	CompanyDTO findByName(String name);
}
