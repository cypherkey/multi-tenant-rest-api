package com.redman.client.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7543140797635384332L;
	
	public ResourceNotFoundException() {
		
	}
	
	public ResourceNotFoundException(String message) {
		super(message);
	}
}