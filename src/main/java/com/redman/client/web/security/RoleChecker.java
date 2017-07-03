package com.redman.client.web.security;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import com.redman.client.web.exceptions.ResourceUnauthorizedException;

@Component("roleChcker")
public class RoleChecker {
	public static String SUPERADMIN = "ROLE_SUPERADMIN";
	public static String COMPANYADMIN = "ROLE_COMPANYADMIN:%s";
	public static String USER = "ROLE_USER:%s";
	public static Logger LOGGER = LoggerFactory.getLogger(RoleChecker.class);

	public static boolean hasValidRole(Principal principal) {
		return hasValidRole(principal, null, null);
	}
	
	public static boolean hasValidRole(Principal principal, String company) {
		return hasValidRole(principal, company, null);
	}

	public static boolean hasValidRole(Principal principal, String company, String user) {
		OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
		
		LOGGER.info("Super role is {}", SUPERADMIN);
		
		if (company != null) {
			LOGGER.info("Required company role is {}", String.format(COMPANYADMIN, company.toUpperCase()));
		}
		
		if (user != null) {
			LOGGER.info("Required user role is {}", String.format(USER, user.toUpperCase()));
		}
		
		for(GrantedAuthority ga : oAuth2Authentication.getAuthorities()) {
			LOGGER.info("Checking {}", ga.getAuthority());
			
			if (ga.getAuthority().equalsIgnoreCase(SUPERADMIN)) {
				return true;
			} else if (company != null && ga.getAuthority().equalsIgnoreCase(String.format(COMPANYADMIN, company.toUpperCase()))) {
				return true;
			} else if (user != null && ga.getAuthority().equalsIgnoreCase(String.format(USER, user.toUpperCase()))) {
				return true;
			}
		}
		throw new ResourceUnauthorizedException();
	}
}
