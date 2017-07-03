package com.redman.client.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.redman.client.data.model.CompanyEntity;

public interface CompanyDao extends JpaRepository<CompanyEntity, Integer> {
	List<CompanyEntity> findAll();
	CompanyEntity findByName(String name);
}