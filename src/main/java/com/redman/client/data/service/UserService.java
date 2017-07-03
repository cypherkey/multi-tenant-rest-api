package com.redman.client.data.service;

import com.redman.client.data.dto.UserDTO;

public interface UserService {
	UserDTO create(UserDTO user);
	UserDTO findByLoginAndCompanyName(String login, String company_name);
}
