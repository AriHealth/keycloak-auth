/*
 * Copyright (C) 2019  All rights reserved.
 * 
 * This file is part of the auth.
 * 
 * TodoApplicationControllerTest.java is free software: you can redistribute it and/or modify it under the 
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
 * @author	Carlos Cavero
 *			Atos Research and Innovation, Atos SPAIN SA
 * 
 * Todo Spring boot Controller tests
 */
package net.atos.ari.auth;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import net.atos.ari.auth.controller.AuthController;
import net.atos.ari.auth.exception.NotAuthorizedException;
import net.atos.ari.auth.model.AccessTokenResponse;
import net.atos.ari.auth.model.KeycloakUser;
import net.atos.ari.auth.service.AuthService;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    /**
     * Init the Mockito annotations and MVC mock.
     * 
     * @return void
     */
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .build();
    }

    private KeycloakUser user;
    private String authToken = "123456789";
    private ObjectWriter ow;

    /**
     * Mock the service calls for the different todos operations.
     * 
     * @return void
     * @throws NotAuthorizedException, when the user is not authorized to 
     *   use the API call 
     */
    @Before
    public void setUp() throws NotAuthorizedException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        ow = mapper.writer()
            .withDefaultPrettyPrinter();

        user = KeycloakUser.builder()
            .username("test")
            .password("test")
            .build();

        Mockito.when(authService.user(""))
            .thenReturn("OK");

        AccessTokenResponse token = new AccessTokenResponse();
        token.setAccess_token(authToken);

        Mockito.when(authService.login(user))
            .thenReturn(token);
    }

    /**
     * Test get user info when no authorization bearer is provided.
     * 
     * @return void
     * @throws Exception, when some error happens 
     */
    @Test
    public void getUserInfoOAuthDisable_returnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
            .header("Authorization", "Bearer " + authToken)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    /**
     * Test login when no authorization bearer is provided.
     * 
     * @return void
     * @throws Exception, when some error happens 
     */
    @Test
    public void login_returnOk() throws Exception {
        String requestAccessToken = ow.writeValueAsString(user);
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestAccessToken.getBytes()))
            .andExpect(status().isOk());
    }
}
