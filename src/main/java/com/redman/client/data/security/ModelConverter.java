package com.redman.client.data.security;

public interface ModelConverter<S, D> {
	D convert(S source);
}
