package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    // Constant strings
    private static final String DEFAULT_USER_NAME = "testUser";
    private static final Long DEFAULT_ITEM_ID = 15L;
    private static final BigDecimal DEFAULT_ITEM_PRICE = new BigDecimal(4);
    private static final int DEFAULT_QUANTITY = 4;
    // declare the mocks
    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    private final CartRepository cartRepositoryMock = mock(CartRepository.class);
    private final ItemRepository itemRepositoryMock = mock(ItemRepository.class);
    // object under test
    private CartController cartController;

    @BeforeClass
    public static void initialize() {
    }

    private static ModifyCartRequest createDefaultTestModifyCartRequest() {
        // Create the user request
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(DEFAULT_USER_NAME);
        modifyCartRequest.setItemId(DEFAULT_ITEM_ID);
        modifyCartRequest.setQuantity(DEFAULT_QUANTITY);

        return modifyCartRequest;
    }

    private static User createTestUser() {
        // Create the user request
        Cart cart = new Cart();
        User testUser = new User();
        testUser.setUsername(DEFAULT_USER_NAME);
        testUser.setPassword("WeakPassword");
        testUser.setId(1L);
        cart.setUser(testUser);
        testUser.setCart(cart);

        return testUser;
    }

    @Before
    public void setUp() {
        cartController = new CartController();
        // Inject the mocks
        TestUtils.injectObjects(cartController, "userRepository", userRepositoryMock);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepositoryMock);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepositoryMock);

    }

    @Test
    public void addToCartHappyPath() {
        // Stub the call on the mocks
        User testUser = createTestUser();
        Item testItem = new Item(DEFAULT_ITEM_ID, "Gogoasa", new BigDecimal(4), "Cu ciocolata");
        when(userRepositoryMock.findByUsername(DEFAULT_USER_NAME)).thenReturn(testUser);
        when(itemRepositoryMock.findById(DEFAULT_ITEM_ID)).thenReturn(Optional.of(testItem));

        // Create the user request
        ModifyCartRequest modifyCartRequest = createDefaultTestModifyCartRequest();

        // Call the method under test
        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart responseCart = response.getBody();
        assertNotNull(responseCart);
        assertNotNull(responseCart.getUser());
        assertEquals(responseCart.getUser().getUsername(), DEFAULT_USER_NAME);
        assertEquals(responseCart.getItems().stream().filter(item -> Objects.equals(item.getId(), DEFAULT_ITEM_ID)).count(), DEFAULT_QUANTITY);
        assertEquals(responseCart.getTotal(), DEFAULT_ITEM_PRICE.multiply(new BigDecimal(DEFAULT_QUANTITY)));

        verify(cartRepositoryMock, times(1)).save(any());
    }

    @Test
    public void addToCartUserNotFound() {
        // Stub the call on the mocks
        when(userRepositoryMock.findByUsername(any())).thenReturn(null);

        // Create the user request
        ModifyCartRequest modifyCartRequest = createDefaultTestModifyCartRequest();

        // Call the method under test
        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        Cart responseItem = response.getBody();
        assertNull(responseItem);

        verify(cartRepositoryMock, times(0)).save(any());
    }

    @Test
    public void addToCartItemNotFound() {
        // Stub the call on the mocks
        User testUser = createTestUser();
        Item testItem = new Item(DEFAULT_ITEM_ID, "Gogoasa", new BigDecimal(4), "Cu ciocolata");
        when(userRepositoryMock.findByUsername(DEFAULT_USER_NAME)).thenReturn(testUser);
        when(itemRepositoryMock.findById(DEFAULT_ITEM_ID)).thenReturn(Optional.empty());

        // Create the user request
        ModifyCartRequest modifyCartRequest = createDefaultTestModifyCartRequest();

        // Call the method under test
        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        Cart responseCart = response.getBody();
        assertNull(responseCart);

        verify(cartRepositoryMock, times(0)).save(any());
    }
}
