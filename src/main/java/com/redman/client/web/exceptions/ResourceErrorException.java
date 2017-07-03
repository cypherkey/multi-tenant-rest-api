package com.redman.client.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceErrorException extends RuntimeException {
	private static final long serialVersionUID = 712340337635384332L;
	
	public ResourceErrorException() {
		
	}
	
	public ResourceErrorException(String message) {
		super(message);
	}
}