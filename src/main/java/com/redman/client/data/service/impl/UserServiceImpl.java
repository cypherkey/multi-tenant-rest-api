package com.redman.client.data.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.redman.client.data.dao.UserDao;
import com.redman.client.data.dto.UserDTO;
import com.redman.client.data.mapper.UserMapper;
import com.redman.client.data.model.UserEntity;
import com.redman.client.data.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
    private UserDao userDao;
	
	public UserDTO create(UserDTO dto) {
		UserEntity entity = UserMapper.mapDTOIntoEntity(dto);
		entity = userDao.saveAndFlush(entity);
		return UserMapper.mapEntityIntoDTO(entity);
	}
	
	public UserDTO findByLoginAndCompanyName(String login, String company_name) {
		return UserMapper.mapEntityIntoDTO(userDao.findByLoginAndCompanyName(login, company_name));
	}
}
