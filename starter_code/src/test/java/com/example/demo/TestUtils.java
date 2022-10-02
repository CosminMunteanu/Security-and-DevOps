package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class TestUtils {

    // Constant strings
    public static final String DEFAULT_USER_NAME = "testUser";
    public static final String DEFAULT_PLAIN_PASSWORD = "testPassword";
    public static final String DEFAULT_HASHED_PASSWORD = "hashedPassword";
    public static final Long DEFAULT_ITEM_ID = 15L;
    public static final BigDecimal DEFAULT_ITEM_PRICE = new BigDecimal(4);
    public static final int DEFAULT_QUANTITY = 4;

    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            // Check the accessibility
            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }
            // Set the objected to be injected into the desired field
            field.set(target, toInject);

            //Restore the accessibility
            if (!wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createTestUserWithEmptyCart() {
        // Create the user request
        Cart cart = new Cart();
        User testUser = new User();
        testUser.setUsername(TestUtils.DEFAULT_USER_NAME);
        testUser.setPassword("WeakPassword");
        testUser.setId(1L);
        cart.setUser(testUser);
        testUser.setCart(cart);

        return testUser;
    }

    public static User createTestUserWithCart() {
        // Create the user request
        Cart cart = new Cart();
        cart.addItem(new Item(TestUtils.DEFAULT_ITEM_ID, "Gogoasa", TestUtils.DEFAULT_ITEM_PRICE, "Cu ciocolata"));
        cart.addItem(new Item(TestUtils.DEFAULT_ITEM_ID + 1, "Bread", new BigDecimal(1), "No salt"));
        User testUser = new User();
        testUser.setUsername(TestUtils.DEFAULT_USER_NAME);
        testUser.setPassword("WeakPassword");
        testUser.setId(1L);
        cart.setUser(testUser);
        testUser.setCart(cart);

        return testUser;
    }
}
