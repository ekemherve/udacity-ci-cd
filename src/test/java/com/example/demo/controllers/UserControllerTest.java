package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    private static final String USERNAME = "herve";
    private final static String PASSWORD = "hervePassword";
    private final static String PASSWORD_LESS_7_CHAR = "five";
    private final static String HASHED_PASSWORD_LESS_7_CHAR = "hfive";
    private final static String HASHED_PASSWORD = "hashedHervePaswword";

    @Before
    public void before() {

        userController = new UserController();
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
    }

    @Test
    public void findByIdTest() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(getUser()));
        ResponseEntity<User> responseEntity = userController.findById(1L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void findByUsername() {

        when(userRepository.findByUsername(USERNAME)).thenReturn(getUser());
        ResponseEntity<User> responseEntity = userController.findByUserName(USERNAME);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(USERNAME, Objects.requireNonNull(responseEntity.getBody()).getUsername());
    }

    @Test
    public void createUserHappyFlowTest() {

        CreateUserRequest createUserRequest = getCreatedUser();
        when(userRepository.save(any())).thenReturn(getUser());
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);

        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        System.out.println(responseEntity.getBody());
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(HASHED_PASSWORD, Objects.requireNonNull(responseEntity.getBody()).getPassword());
    }

    @Test
    public void createUserWithPasswordLengthLessThan7CharTest() {

        CreateUserRequest createUserRequest = getCreatedUser();
        createUserRequest.setPassword(PASSWORD_LESS_7_CHAR);
        createUserRequest.setConfirmPassword(HASHED_PASSWORD_LESS_7_CHAR);

        User user = getUser();
        user.setPassword(HASHED_PASSWORD_LESS_7_CHAR);

        when(userRepository.save(any())).thenReturn(user);
        when(bCryptPasswordEncoder.encode(PASSWORD_LESS_7_CHAR)).thenReturn(HASHED_PASSWORD_LESS_7_CHAR);

        ResponseEntity<User> responseEntity = userController.createUser(createUserRequest);

        System.out.println(responseEntity.getBody());
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    private CreateUserRequest getCreatedUser() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setConfirmPassword(PASSWORD);
        return createUserRequest;
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(HASHED_PASSWORD);
        return user;
    }
}
