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

    // Constant strings
    private static final String DEFAULT_USER_NAME = "testUser";
    private static final String DEFAULT_PLAIN_PASSWORD = "testPassword";
    private static final String DEFAULT_HASHED_PASSWORD = "hashedPassword";
    private static User testUser;
    // declare the mocks
    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private UserController userController;

    @BeforeClass
    public static void initialize() {
        testUser = new User();
        testUser.setUsername(DEFAULT_USER_NAME);
        testUser.setPassword(DEFAULT_HASHED_PASSWORD);
        testUser.setId(0);
    }

    private static CreateUserRequest createDefaultTestUserRequest() {
        // Create the user request
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setUsername(DEFAULT_USER_NAME);
        userReq.setPassword(DEFAULT_PLAIN_PASSWORD);
        userReq.setConfirmPassword(DEFAULT_PLAIN_PASSWORD);

        return userReq;
    }

    @Before
    public void setUp() {
        userController = new UserController();
        // Inject the mocks
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "passwordEncoder", encoder);
    }

    @Test
    public void createUserHappyPath() {
        // Stub
        when(encoder.encode(DEFAULT_PLAIN_PASSWORD)).thenReturn(DEFAULT_HASHED_PASSWORD);
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
        assertEquals(DEFAULT_USER_NAME, user.getUsername());
        assertEquals(DEFAULT_HASHED_PASSWORD, user.getPassword());
    }

    @Test
    public void createUserNullPassword() {
        // Stub
        when(encoder.encode(DEFAULT_PLAIN_PASSWORD)).thenReturn(DEFAULT_PLAIN_PASSWORD);
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
        when(encoder.encode(DEFAULT_PLAIN_PASSWORD)).thenReturn(DEFAULT_HASHED_PASSWORD);
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
        when(encoder.encode(DEFAULT_PLAIN_PASSWORD)).thenReturn(DEFAULT_HASHED_PASSWORD);
        // Create the user request
        CreateUserRequest userReq = createDefaultTestUserRequest();
        userReq.setConfirmPassword(DEFAULT_HASHED_PASSWORD);

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
        when(userRepo.findByUsername(DEFAULT_USER_NAME)).thenReturn(testUser);

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
        assertEquals(DEFAULT_USER_NAME, user.getUsername());
        assertEquals(DEFAULT_HASHED_PASSWORD, user.getPassword());
    }

    @Test
    public void findUserByNameUserNotFound() {
        // Stubs
        when(userRepo.findByUsername(DEFAULT_USER_NAME)).thenReturn(null);

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
        when(userRepo.findById(0L)).thenReturn(Optional.empty());

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
        when(userRepo.findById(0L)).thenReturn(Optional.of(testUser));

        // Call the method under test
        final ResponseEntity<User> response = userController.findById(0L);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals(DEFAULT_USER_NAME, user.getUsername());
        assertEquals(DEFAULT_HASHED_PASSWORD, user.getPassword());
    }
}
