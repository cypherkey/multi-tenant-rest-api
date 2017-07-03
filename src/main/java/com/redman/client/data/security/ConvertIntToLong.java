package com.redman.client.data.security;

public class ConvertIntToLong implements ModelConverter<Integer, Long> {
	public Long convert(Integer source) {
		return new Long(source);
	}
}