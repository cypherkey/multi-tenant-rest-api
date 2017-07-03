package com.redman.client.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;



@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	
	@Autowired
	DataSource datasource;

	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			 .antMatchers(HttpMethod.GET,"/foos").access("#oauth2.hasScope('read')")
			 .antMatchers(HttpMethod.GET,"/bar").access("#oauth2.hasScope('read')")
			 .antMatchers(HttpMethod.GET,"/resource").access("#oauth2.hasScope('write')")
			 .antMatchers(HttpMethod.GET,"/test").access("#oauth2.hasScope('write')")
			.anyRequest().authenticated().
			 and().csrf().disable();
		
		
	}
	
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources)
			throws Exception {

		resources.resourceId("resource");
		
	}

	@Bean
	public AccessTokenConverter accessTokenConverter() {
		return new DefaultAccessTokenConverter();
	}
	
	   @Bean
	   public RemoteTokenServices LocalTokenService() {
	        final RemoteTokenServices tokenService = new RemoteTokenServices();
	        tokenService.setCheckTokenEndpointUrl("http://localhost:8081/oauth/check_token");
	        tokenService.setClientId("my-client-with-secret");
	        tokenService.setClientSecret("secret");
	        return tokenService;
	    }
	

}
