package com.redman.client.data.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ModelMapper {
	public String[] readRoles() default {};
	public String[] writeRoles() default {};
	public String readConverter() default "";
	public String writeConverter() default "";
}