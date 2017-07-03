package com.redman.client.web.security;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface SecurableDTO {
	@JsonIgnore
	String[] getRequiredRoles();
}
