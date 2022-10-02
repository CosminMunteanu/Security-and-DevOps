package com.example.demo.controllers;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    // Static members used in tests
    private static User testUser;

    // declare the mocks
    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    private final CartRepository cartRepositoryMock = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoderMock = mock(BCryptPasswordEncoder.class);

    // object under test
    private UserController userController;

    @BeforeClass
    public static void initialize() {
        testUser = new User();
        testUser.setUsername(TestUtils.DEFAULT_USER_NAME);
        testUser.setPassword(TestUtils.DEFAULT_HASHED_PASSWORD);
        testUser.setId(0);
    }

    private static CreateUserRequest createDefaultTestUserRequest() {
        // Create the user request
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setUsername(TestUtils.DEFAULT_USER_NAME);
        userReq.setPassword(TestUtils.DEFAULT_PLAIN_PASSWORD);
        userReq.setConfirmPassword(TestUtils.DEFAULT_PLAIN_PASSWORD);

        return userReq;
    }

    @Before
    public void setUp() {
        userController = new UserController();
        // Inject the mocks
        TestUtils.injectObjects(userController, "userRepository", userRepositoryMock);
        TestUtils.injectObjects(userController, "cartRepository", cartRepositoryMock);
        TestUtils.injectObjects(userController, "passwordEncoder", encoderMock);
    }

    @Test
    public void createUserHappyPath() {
        // Stub
        when(encoderMock.encode(TestUtils.DEFAULT_PLAIN_PASSWORD)).thenReturn(TestUtils.DEFAULT_HASHED_PASSWORD);
        // Create the user request
        CreateUserRequest userReq = createDefaultTestUserRequest();

        // Call the method under test
        final ResponseEntity<User> response = userController.createUser(userReq);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(TestUtils.DEFAULT_USER_NAME, user.getUsername());
        assertEquals(TestUtils.DEFAULT_HASHED_PASSWORD, user.getPassword());
    }

    @Test
    public void createUserNullPassword() {
        // Stub
        when(encoderMock.encode(TestUtils.DEFAULT_PLAIN_PASSWORD)).thenReturn(TestUtils.DEFAULT_PLAIN_PASSWORD);
        // Create the user request
        CreateUserRequest userReq = createDefaultTestUserRequest();
        userReq.setPassword(null);

        // Call the method under test
        final ResponseEntity<User> response = userController.createUser(userReq);

        // Assertions
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void createUserInvalidPasswordLength() {
        // Stub
        when(encoderMock.encode(TestUtils.DEFAULT_PLAIN_PASSWORD)).thenReturn(TestUtils.DEFAULT_HASHED_PASSWORD);
        // Create the user request
        CreateUserRequest userReq = createDefaultTestUserRequest();
        userReq.setPassword("Cosm");
        userReq.setConfirmPassword("Cosm");

        // Call the method under test
        final ResponseEntity<User> response = userController.createUser(userReq);

        // Assertions
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void createUserInvalidConfirmPassword() {
        // Stub
        when(encoderMock.encode(TestUtils.DEFAULT_PLAIN_PASSWORD)).thenReturn(TestUtils.DEFAULT_HASHED_PASSWORD);
        // Create the user request
        CreateUserRequest userReq = createDefaultTestUserRequest();
        userReq.setConfirmPassword(TestUtils.DEFAULT_HASHED_PASSWORD);

        // Call the method under test
        final ResponseEntity<User> response = userController.createUser(userReq);

        // Assertions
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void findUserByNameHappyPath() {
        // Stubs
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);

        // Create the user request
        CreateUserRequest userReq = createDefaultTestUserRequest();

        // Call the method under test
        final ResponseEntity<User> response = userController.findByUserName(userReq.getUsername());

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(TestUtils.DEFAULT_USER_NAME, user.getUsername());
        assertEquals(TestUtils.DEFAULT_HASHED_PASSWORD, user.getPassword());
    }

    @Test
    public void findUserByNameUserNotFound() {
        // Stubs
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(null);

        // Create the user request
        CreateUserRequest userReq = createDefaultTestUserRequest();

        // Call the method under test
        final ResponseEntity<User> response = userController.findByUserName(userReq.getUsername());

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void findUserByIdUserNotFound() {
        // Stubs
        when(userRepositoryMock.findById(0L)).thenReturn(Optional.empty());

        // Call the method under test
        final ResponseEntity<User> response = userController.findById(0L);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void findUserByIdHappyPath() {
        // Stubs
        when(userRepositoryMock.findById(0L)).thenReturn(Optional.of(testUser));

        // Call the method under test
        final ResponseEntity<User> response = userController.findById(0L);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(TestUtils.DEFAULT_USER_NAME, user.getUsername());
        assertEquals(TestUtils.DEFAULT_HASHED_PASSWORD, user.getPassword());
    }
}
