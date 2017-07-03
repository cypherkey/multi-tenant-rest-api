package com.redman.client.data.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.redman.client.data.model.UserEntity;

public interface UserDao extends JpaRepository<UserEntity, Integer> {
	List<UserEntity> findAll();
	UserEntity findByLoginAndCompanyName(String login, String company_name);
}