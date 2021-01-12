/*
 * Copyright (C) 2018  Atos Spain SA. All rights reserved.
 * 
 * This file is part of the KeyCloak Auth API.
 * 
 * AuthController.java is free software: you can redistribute it and/or modify it under the 
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
 * Spring boot controller for KeyCloak auth
 */

package net.atos.ari.auth.controller;

import org.slf4j.Logger;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.http.HttpHeaders;

import net.atos.ari.auth.exception.NotAuthorizedException;
import net.atos.ari.auth.model.AccessTokenResponse;
import net.atos.ari.auth.model.KeycloakUser;
import net.atos.ari.auth.service.Service;

@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = {"https://portal.fair4health.eu","https://testbed.fair4health.eu","http://localhost:4200"}, methods= {RequestMethod.GET,RequestMethod.POST})
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    Service authService;

    @ApiOperation(value = "Give OAuth access token given user and password...")
    @PostMapping("/login")
    public AccessTokenResponse login(@RequestBody KeycloakUser user) throws NotAuthorizedException {
        log.info("Login user");
        return authService.login(user);
    }

    @ApiOperation(value = "Provide the user preferred name given the token.")
    @GetMapping("/user")
    public String user(@ApiParam(value = "Bearer <token>") 
    	@RequestHeader(HttpHeaders.AUTHORIZATION) String token) throws NotAuthorizedException {
        log.info("Get User info");
        return authService.user(token);
    }

    
    @ApiOperation(value = "Provide token info validation.")
    @PostMapping("/isvalid")
    public boolean isValid(String token) 
    		throws NotAuthorizedException {
        log.info("Get isvalid token");
        return authService.isValid(token);
    }
}