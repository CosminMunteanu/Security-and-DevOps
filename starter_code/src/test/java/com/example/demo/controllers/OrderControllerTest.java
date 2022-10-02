package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderControllerTest {
    // declare the mocks
    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    private final OrderRepository orderRepositoryMock = mock(OrderRepository.class);

    // object under test
    private OrderController orderController;

    @Before
    public void setUp() {
        orderController = new OrderController();
        // Inject the mocks
        TestUtils.injectObjects(orderController, "userRepository", userRepositoryMock);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepositoryMock);
    }

    @Test
    public void submitOrderHappyPath() {
        // Stub the call on the mocks
        User testUser = TestUtils.createTestUserWithCart();
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);


        // Call the method under test
        final ResponseEntity<UserOrder> response = orderController.submit(TestUtils.DEFAULT_USER_NAME);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder responseOrder = response.getBody();
        assertNotNull(responseOrder);
        assertEquals(responseOrder.getUser(), testUser);
        assertEquals(responseOrder.getUser().getUsername(), TestUtils.DEFAULT_USER_NAME);
        assertTrue(responseOrder.getItems().containsAll(testUser.getCart().getItems()));
        assertEquals(responseOrder.getTotal(), testUser.getCart().getTotal());

        verify(orderRepositoryMock, times(1)).save(any());
    }

    @Test
    public void submitOrderUserNotFound() {
        // Stub the call on the mocks
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(null);

        // Call the method under test
        final ResponseEntity<UserOrder> response = orderController.submit(TestUtils.DEFAULT_USER_NAME);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        UserOrder responseOrder = response.getBody();
        assertNull(responseOrder);

        verify(orderRepositoryMock, times(0)).save(any());
    }

    @Test
    public void submitOrderCartIsEmpty() {
        // Stub the call on the mocks
        User testUser = TestUtils.createTestUserWithEmptyCart();
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);

        // Call the method under test
        final ResponseEntity<UserOrder> response = orderController.submit(TestUtils.DEFAULT_USER_NAME);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        UserOrder responseOrder = response.getBody();
        assertNull(responseOrder);

        verify(orderRepositoryMock, times(0)).save(any());
    }

    @Test
    public void getOrdersHappyPath() {
        // Stub the call on the mocks
        User testUser = TestUtils.createTestUserWithCart();
        List<UserOrder> testOrderList = new ArrayList<>();
        testOrderList.add(UserOrder.createFromCart(testUser.getCart()));
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);
        when(orderRepositoryMock.findByUser(testUser)).thenReturn(testOrderList);

        // Call the method under test
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(TestUtils.DEFAULT_USER_NAME);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> responseOrder = response.getBody();
        assertNotNull(responseOrder);
        assertTrue(responseOrder.containsAll(testOrderList));
    }

    @Test
    public void getOrdersHappyUserNotFound() {
        // Stub the call on the mocks
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(null);

        // Call the method under test
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(TestUtils.DEFAULT_USER_NAME);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        List<UserOrder> responseOrder = response.getBody();
        assertNull(responseOrder);
    }

    @Test
    public void getOrdersOrderNotFound() {
        // Stub the call on the mocks
        User testUser = TestUtils.createTestUserWithCart();
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);
        when(orderRepositoryMock.findByUser(testUser)).thenReturn(null);
        
        // Call the method under test
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(TestUtils.DEFAULT_USER_NAME);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> responseOrder = response.getBody();
        assertNull(responseOrder);
    }
}
