package com.redman.client.web.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.redman.client.data.dto.CompanyDTO;
import com.redman.client.data.dto.UserDTO;
import com.redman.client.data.service.CompanyService;
import com.redman.client.data.service.UserService;
import com.redman.client.web.exceptions.ResourceNotFoundException;
import com.redman.client.web.security.RoleChecker;

@RestController
@EnableResourceServer
public class ResourcesController {
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	UserService userService;
	
	@RequestMapping(value="/foo", method=RequestMethod.GET, produces=MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> foo(Principal principal) {
		StringBuilder sb = new StringBuilder();
		OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
		sb.append("Name=");
		sb.append(oAuth2Authentication.getName());
		sb.append("\r\n");
		sb.append("Authorities:");
		for(GrantedAuthority ga : oAuth2Authentication.getAuthorities()) {
			sb.append(ga.getAuthority());
			sb.append("\r\n");
		}
		return new ResponseEntity<String>(sb.toString(), HttpStatus.OK);
	}

	@PreAuthorize("@roleChecker.hasValidRole(#principal)")
	@RequestMapping(value="/company", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CompanyDTO>> listCompany(Principal principal) {
		
		// Check for SUPERADMIN role
		// RoleChecker.hasValidRole(principal);
		
		List<CompanyDTO> companies = companyService.findAll();

		return new ResponseEntity<List<CompanyDTO>>(companies, HttpStatus.OK);
	}

	@PreAuthorize("@roleChecker.hasValidRole(#principal)")
	@RequestMapping(value="/company", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompanyDTO> createCompany(
			Principal principal,
			@RequestBody CompanyDTO companyDTO) {
		
		// Check for SUPERADMIN role
		// RoleChecker.hasValidRole(principal);
		
		companyDTO = companyService.create(companyDTO);
		
		return new ResponseEntity<CompanyDTO>(companyDTO, HttpStatus.CREATED);
	}

	@PreAuthorize("@roleChecker.hasValidRole(#principal, #companyid)")
	@RequestMapping(value="/company/{companyid}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CompanyDTO> getCompany(
			Principal principal,
			@PathVariable String companyid) {
		
		// Check for SUPERADMIN and COMPANYADMIN role
		// RoleChecker.hasValidRole(principal, companyid);
		
		CompanyDTO companyDTO = companyService.findByName(companyid);
		if (companyDTO == null) {
			throw new ResourceNotFoundException();
		}
		
		return new ResponseEntity<CompanyDTO>(companyDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value="/company/{companyid}/user", method=RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDTO> createCompanyUser(
			Principal principal,
			@PathVariable String companyid,
			@RequestBody UserDTO userDTO) {
		
		// Check for SUPERADMIN and COMPANYADMIN role
		RoleChecker.hasValidRole(principal, companyid);
		
		userDTO = userService.create(userDTO);
		
		return new ResponseEntity<UserDTO>(userDTO, HttpStatus.CREATED);
	}

	@RequestMapping(value="/company/{companyid}/user/{userid}", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDTO> getCompanyUser(
			Principal principal, 
			@PathVariable String companyid, 
			@PathVariable String userid) {
		
		// Check for SUPERADMIN and COMPANYADMIN role and USER role
		RoleChecker.hasValidRole(principal, companyid, userid);
		
		UserDTO userDTO = userService.findByLoginAndCompanyName(userid, companyid);
		if (userDTO == null) {
			throw new ResourceNotFoundException();
		}
		
		return new ResponseEntity<UserDTO>(userDTO, HttpStatus.OK);
	}
}
