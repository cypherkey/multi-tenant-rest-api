package com.redman.client.data.dto;

import com.redman.client.data.security.ModelMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	protected String companyName;
    protected String login;
    
    @ModelMapper(readRoles = {"ROLE_SUPERADMIN", "ROLE_COMPANYADMIN"}, writeRoles = {"ROLE_SUPERADMIN", "ROLE_COMPANYADMIN"} )
    protected String password;

    @ModelMapper(readRoles = {"ROLE_SUPERADMIN"}, writeRoles = {"ROLE_SUPERADMIN"} )
    protected Long quota;

    @ModelMapper(readRoles = {"ROLE_SUPERADMIN"}, writeRoles = {"ROLE_SUPERADMIN"} )
    protected Boolean enabled;
}
