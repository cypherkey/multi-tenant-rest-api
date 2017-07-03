package com.redman.client.data.security;

public class ConvertLongToInt implements ModelConverter<Long, Integer> {
	public Integer convert(Long source) {
		return source.intValue();
	}
}