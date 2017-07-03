package com.redman.client.data.security;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.redman.client.data.dto.CompanyDTO;
import com.redman.client.data.dto.UserDTO;

public class ModelDTOMapper {
	public static Logger LOGGER = LoggerFactory.getLogger(ModelDTOMapper.class);
	
	public static void readEntityFieldToDto(Object dto, Object entity, String fieldName) {
		readEntityFieldToDto(dto, fieldName, entity, fieldName, null);
	}
	
	public static void readEntityFieldToDto(Object dto, String dtoFieldName, Object entity, String entityFieldName, ModelConverter converter) throws IllegalArgumentException {
		Field dtoField = getField(dto, dtoFieldName);
		ModelMapper a = dtoField.getAnnotation(ModelMapper.class);

		if (a == null || a.readRoles().length == 0 || checkSecurity(getRequiredRoles(dto, a.readRoles())) ) {
			copyValue(entity, entityFieldName, dto, dtoFieldName, converter);
		}
	}
	
	public static void writeDtoFieldToEntity(Object dto, Object entity, String fieldName) {
		writeDtoFieldToEntity(dto, fieldName, entity, fieldName, null);
	}
	
	public static void writeDtoFieldToEntity(Object dto, String dtoFieldName, Object entity, String entityFieldName, ModelConverter converter) {
		Field dtoField = getField(dto, dtoFieldName);
		ModelMapper a = dtoField.getAnnotation(ModelMapper.class);

		if (a == null || a.writeRoles().length == 0 || checkSecurity(getRequiredRoles(dto, a.writeRoles())) ) {
			copyValue(dto, dtoFieldName, entity, entityFieldName, converter);
		}
	}
	
	private static Boolean checkSecurity(List<String> requiredRoles) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for(GrantedAuthority authority : authorities) {
			if (requiredRoles.contains(authority.getAuthority())) {
				LOGGER.info("Authority {} is in the list of required roles", authority.getAuthority());
				return true;
			}
		}
		return false;
	}
	
	private static List<String> getRequiredRoles(Object obj, String[] annotationValues) {
		String company = null;
		String user = null;
		
		if (obj instanceof UserDTO) {
			UserDTO u = (UserDTO)obj;
			company = u.getCompanyName();
			user = u.getLogin();
		} else if (obj instanceof CompanyDTO) {
			CompanyDTO c = (CompanyDTO)obj;
			company = c.getName();
		}
		
		List<String> requiredRoles = new ArrayList<String>();
		for(String annotationValue : annotationValues) {
			if (annotationValue.equalsIgnoreCase("ROLE_SUPERADMIN") || annotationValue.equalsIgnoreCase("ROLE_ADMIN")) {
				requiredRoles.add(annotationValue.toUpperCase());
			} else if (annotationValue.equalsIgnoreCase("ROLE_COMPANYADMIN") && company != null) {
				requiredRoles.add(String.format("%s:%s", annotationValue.toUpperCase(), company.toUpperCase()));
			} else if (annotationValue.equalsIgnoreCase("ROLE_USER") && user != null) {
				requiredRoles.add(String.format("%s:%s", annotationValue.toUpperCase(), user.toUpperCase()));
			}
		}
		
		return requiredRoles;
	}

	private static Field getField(Object obj, String fieldName) {
		try {
			return obj.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException ex) {
			throw new IllegalArgumentException(String.format("Object %s does not contain field %s", obj.getClass().getName(), fieldName));
		}
	}
	
	private static Boolean hasField(Object obj, String fieldName) {
    	try {
    		obj.getClass().getDeclaredField(fieldName);
    		return true;
    	} catch(NoSuchFieldException ex) {
    		return false;
    	}
    }
    
    private static void setValue(Object obj, String field, Object value) {
    	LOGGER.info("Setting value for field {}", field);
    	try {
    		Field f = obj.getClass().getDeclaredField(field);
    		LOGGER.info("  field type is {}", f.getType().getName());

    		// Try and use a setter
    		for(Method m : obj.getClass().getDeclaredMethods()) {
    			if (m.getParameterCount() != 1) {
    				LOGGER.info("  Skipping {} as it has {} parameters", m.getName(), m.getParameterCount());
    				continue;
    			}
    			LOGGER.info("  checking dst method {} with param count {}", m.getName(), m.getParameterCount());
    			LOGGER.info("  method has parameter type of {}", 
						m.getParameters()[0].getType().getName());

    			if (m.getName().toLowerCase().equals(String.format("set%s", field)) &&
    					m.getParameters()[0].getType().getName().equals(f.getType().getName())) {
    				LOGGER.info("  using method {}", m.getName());
    				m.invoke(obj, value);
    				return;
    			}
    		}
    		
    		// Use the direct method
    		LOGGER.info("  using direct method");
    		f.setAccessible(true);
    		f.set(obj, value);
    	} catch(NoSuchFieldException ex) {
    		throw new IllegalArgumentException(ex); 
    	} catch(InvocationTargetException ex) {
    		throw new IllegalArgumentException(ex); 
    	} catch(IllegalAccessException ex) {
    		throw new IllegalArgumentException(ex); 
    	}
    }

    private static Object getValue(Object obj, String field) {
    	LOGGER.info("Getting value for field {}", field);
    	try {
	    	Field f = obj.getClass().getDeclaredField(field);
	    	LOGGER.info("  field type is {}", f.getType().getName());
	    	
	    	// Try and use a getter
	    	for(Method m : obj.getClass().getDeclaredMethods()) {
				LOGGER.info("  checking src method {} with return type of {}", m.getName(), m.getReturnType().getName());
				
				if (m.getName().toLowerCase().equals(String.format("get%s", field.toLowerCase())) &&
						m.getReturnType().getName().equals(f.getType().getName()))
				{
					LOGGER.info("  using method {}", m.getName());
					return m.invoke(obj);
				}
			}
	    	
	    	// Use the direct method
	    	LOGGER.info("  using direct method");
	    	f.setAccessible(true);
	    	return f.get(obj);
    	} catch(NoSuchFieldException ex) {
    		throw new IllegalArgumentException(ex); 
    	} catch(InvocationTargetException ex) {
    		throw new IllegalArgumentException(ex); 
    	} catch(IllegalAccessException ex) {
    		throw new IllegalArgumentException(ex); 
    	}
    }
    
    private static void copyValue(Object src, String srcField, Object dst, String dstField, ModelConverter converter) {
    	Object val = getValue(src, srcField);
    	if (converter != null) {
    		val = converter.convert(val);
    	}
    	setValue(dst, dstField, val);
    }
}
