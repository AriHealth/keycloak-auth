/*
 * Copyright (C) 2018  Atos Spain SA. All rights reserved.
 * 
 * This file is part of the Auth project.
 * 
 * This is free software: you can redistribute it and/or modify it under the 
 * terms of the Apache License, Version 2.0 (the License);
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * The software is provided "AS IS", without any warranty of any kind, express or implied,
 * including but not limited to the warranties of merchantability, fitness for a particular
 * purpose and noninfringement, in no event shall the authors or copyright holders be 
 * liable for any claim, damages or other liability, whether in action of contract, tort or
 * otherwise, arising from, out of or in connection with the software or the use or other
 * dealings in the software.
 * 
 * See README file for the full disclaimer information and LICENSE file for full license 
 * information in the project root.
 * 
 * Auth Spring boot application
 */
package net.atos.ari.auth.service;

import org.apache.http.HttpStatus;
import org.keycloak.representations.AccessToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import net.atos.ari.auth.exception.NotAuthorizedException;
import net.atos.ari.auth.model.AccessTokenResponse;
import net.atos.ari.auth.model.KeycloakUser;

@Component
public class AuthService implements Service {

    public static final Logger log = LoggerFactory.getLogger(AuthService.class);

    // Constants
    @Value("${keycloak.url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.client_id}")
    private String keycloakClientId;
    
    @Value("${keycloak.client_secret}")
    private String clientSecret;
        

    RestTemplate restTemplate = new RestTemplate();
    private static final String BEARER = "BEARER ";

    @Override
    public AccessTokenResponse login(KeycloakUser user) throws NotAuthorizedException {
        try {
            String uri = keycloakUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token/";
            String data = "grant_type=password&username=" + user.getUsername() + 
                "&password=" + user.getPassword() 
                + "&client_id=" + keycloakClientId
                + "&client_secret=" + clientSecret;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/x-www-form-urlencoded");

            HttpEntity<String> entity = new HttpEntity<String>(data, headers);
            ResponseEntity<AccessTokenResponse> response = 
                restTemplate.exchange(uri, HttpMethod.POST, entity, AccessTokenResponse.class);

            if (response.getStatusCode()
                .value() != HttpStatus.SC_OK) {
                log.error("Unauthorised access to protected resource", response.getStatusCode()
                    .value());
                throw new NotAuthorizedException("Unauthorised access to protected resource");
            }
            return response.getBody();
        } catch (Exception ex) {
            log.error("Unauthorised access to protected resource", ex);
            throw new NotAuthorizedException("Unauthorised access to protected resource");
        }
    }

   
    @Override
	public Boolean isValid(String authToken) throws NotAuthorizedException {
    	String uri = keycloakUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token/introspect";
    	 
    	 String data = "&client_id=" + keycloakClientId
                 + "&client_secret=" + clientSecret
                 + "&token_type_hint=access_token"
    	 		 + "&token="+authToken;
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/x-www-form-urlencoded");

		 HttpEntity<String> entity = new HttpEntity<String>(data, headers);
		 log.debug("Token info: {}", entity);
		 
          ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(uri, HttpMethod.POST, 
        		  entity, AccessTokenResponse.class);
        
         
         if (response.getStatusCode()
             .value() != HttpStatus.SC_OK) {
             log.error("Unauthorised access to protected resource", response.getStatusCode()
                 .value());
             throw new NotAuthorizedException("Unauthorised access to protected resource");
         }

         if (response.getBody() != null) {
        	 log.debug("Token info: {}", response.getBody());
        	 return response.getBody().isActive();
         }
         else {
         	new NotAuthorizedException("OAuth2 Authentication failure. ");
         }
         return false;
	}
	
   
    
    @Override
    public String user(String authToken) throws NotAuthorizedException {

        if (!authToken.toUpperCase()
            .startsWith(BEARER)) {
            throw new NotAuthorizedException("Invalid OAuth Header. Missing Bearer prefix");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<AccessToken> response = restTemplate.exchange(keycloakUrl + 
            "/realms/" + keycloakRealm + "/protocol/openid-connect/userinfo", HttpMethod.POST, entity, AccessToken.class);

        if (response.getStatusCode()
            .value() != HttpStatus.SC_OK) {
            log.error("OAuth2 Authentication failure. " + 
                "Invalid OAuth Token supplied in Authorization Header on Request. Code {}", 
                response.getStatusCode().value());
            throw new NotAuthorizedException("OAuth2 Authentication failure. " + 
                "Invalid OAuth Token supplied in Authorization Header on Request.");
        }

        if (response.getBody() != null) {
        	log.debug("User info: {}", response.getBody()
        			.getPreferredUsername());
        	return response.getBody()
        			.getPreferredUsername();
        }
        else {
        	new NotAuthorizedException("OAuth2 Authentication failure. ");
        }
        return "";
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}