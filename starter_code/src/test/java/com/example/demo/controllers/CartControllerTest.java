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
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    // declare the mocks
    private final UserRepository userRepositoryMock = mock(UserRepository.class);
    private final CartRepository cartRepositoryMock = mock(CartRepository.class);
    private final ItemRepository itemRepositoryMock = mock(ItemRepository.class);
    
    // object under test
    private CartController cartController;

    private static ModifyCartRequest createDefaultTestModifyCartRequest() {
        // Create the user request
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(TestUtils.DEFAULT_USER_NAME);
        modifyCartRequest.setItemId(TestUtils.DEFAULT_ITEM_ID);
        modifyCartRequest.setQuantity(TestUtils.DEFAULT_QUANTITY);

        return modifyCartRequest;
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
        User testUser = TestUtils.createTestUserWithEmptyCart();
        Item testItem = new Item(TestUtils.DEFAULT_ITEM_ID, "Gogoasa", TestUtils.DEFAULT_ITEM_PRICE, "Cu ciocolata");
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);
        when(itemRepositoryMock.findById(TestUtils.DEFAULT_ITEM_ID)).thenReturn(Optional.of(testItem));

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
        assertEquals(responseCart.getUser().getUsername(), TestUtils.DEFAULT_USER_NAME);
        assertEquals(responseCart.getItems().stream().filter(item -> Objects.equals(item.getId(), TestUtils.DEFAULT_ITEM_ID)).count(), TestUtils.DEFAULT_QUANTITY);
        assertEquals(responseCart.getTotal(), TestUtils.DEFAULT_ITEM_PRICE.multiply(new BigDecimal(TestUtils.DEFAULT_QUANTITY)));

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
        User testUser = TestUtils.createTestUserWithEmptyCart();
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);
        when(itemRepositoryMock.findById(TestUtils.DEFAULT_ITEM_ID)).thenReturn(Optional.empty());

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

    @Test
    public void removeFromCartHappyPath() {
        // Stub the call on the mocks
        User testUser = TestUtils.createTestUserWithCart();
        Item testItem = new Item(TestUtils.DEFAULT_ITEM_ID, "Gogoasa", TestUtils.DEFAULT_ITEM_PRICE, "Cu ciocolata");
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);
        when(itemRepositoryMock.findById(TestUtils.DEFAULT_ITEM_ID)).thenReturn(Optional.of(testItem));

        // Create the user request
        ModifyCartRequest modifyCartRequest = createDefaultTestModifyCartRequest();

        // Call the method under test
        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart responseCart = response.getBody();
        assertNotNull(responseCart);
        assertNotNull(responseCart.getUser());
        assertEquals(responseCart.getUser().getUsername(), TestUtils.DEFAULT_USER_NAME);
        assertEquals(responseCart.getItems().stream().filter(item -> Objects.equals(item.getId(), TestUtils.DEFAULT_ITEM_ID)).count(), 0);
        assertEquals(responseCart.getTotal(), new BigDecimal(1));

        verify(cartRepositoryMock, times(1)).save(any());
    }

    @Test
    public void removeFromCartUserNotFound() {
        // Stub the call on the mocks
        Item testItem = new Item(TestUtils.DEFAULT_ITEM_ID, "Gogoasa", TestUtils.DEFAULT_ITEM_PRICE, "Cu ciocolata");
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(null);
        when(itemRepositoryMock.findById(TestUtils.DEFAULT_ITEM_ID)).thenReturn(Optional.of(testItem));

        // Create the user request
        ModifyCartRequest modifyCartRequest = createDefaultTestModifyCartRequest();

        // Call the method under test
        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        Cart responseCart = response.getBody();
        assertNull(responseCart);

        verify(cartRepositoryMock, times(0)).save(any());
    }

    @Test
    public void removeFromCartItemNotFound() {
        // Stub the call on the mocks
        User testUser = TestUtils.createTestUserWithCart();
        when(userRepositoryMock.findByUsername(TestUtils.DEFAULT_USER_NAME)).thenReturn(testUser);
        when(itemRepositoryMock.findById(TestUtils.DEFAULT_ITEM_ID)).thenReturn(Optional.empty());

        // Create the user request
        ModifyCartRequest modifyCartRequest = createDefaultTestModifyCartRequest();

        // Call the method under test
        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        Cart responseCart = response.getBody();
        assertNull(responseCart);
        assertEquals(testUser.getCart().getTotal(), new BigDecimal(5));

        verify(cartRepositoryMock, times(0)).save(any());
    }
}
