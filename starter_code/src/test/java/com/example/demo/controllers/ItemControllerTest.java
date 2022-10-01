package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    // private static members
    private final static Long firstItemId = 1L;
    private final static List<Item> testItems = new ArrayList<>(Arrays.asList(
            new Item(firstItemId, "Item1", new BigDecimal("12.7"), "Item bun"),
            new Item(firstItemId + 1, "Item2", new BigDecimal(0), "Item gratis"),
            new Item(firstItemId + 2, "Item3", new BigDecimal(-1), "Item bad price")
    ));
    // declare the mocks
    private final ItemRepository itemRepository = mock(ItemRepository.class);
    private ItemController itemController;

    @BeforeClass
    public static void initialize() {
    }

    @Before
    public void setUp() {
        itemController = new ItemController();
        // Inject the mocks
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getAllItemsHappyPath() {
        // Stub
        when(itemRepository.findAll()).thenReturn(testItems);

        // Call the method under test
        final ResponseEntity<List<Item>> response = itemController.getItems();

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(responseItems.size(), testItems.size());
        assertTrue(responseItems.stream().anyMatch(testItems::contains));
        assertTrue(responseItems.containsAll(testItems));
        assertTrue(testItems.containsAll(responseItems));
    }

    @Test
    public void getAllItemsNoItems() {
        // Stub
        when(itemRepository.findAll()).thenReturn(null);

        // Call the method under test
        final ResponseEntity<List<Item>> response = itemController.getItems();

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNull(items);
    }

    @Test
    public void getItemByIdHappyPath() {
        // Test variables
        Item testItem = testItems.get(0);
        assert testItem != null;

        // Stub
        when(itemRepository.findById(firstItemId)).thenReturn(Optional.of(testItem));

        // Call the method under test
        final ResponseEntity<Item> response = itemController.getItemById(firstItemId);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item responseItem = response.getBody();
        assertNotNull(responseItem);
        assertEquals(responseItem.getId(), testItem.getId());
        assertEquals(responseItem.getName(), testItem.getName());
        assertEquals(responseItem.getPrice(), testItem.getPrice());
        assertEquals(responseItem.getDescription(), testItem.getDescription());
    }

    @Test
    public void getItemByIdItemNotFound() {
        // Stub
        when(itemRepository.findById(firstItemId)).thenReturn(Optional.empty());

        // Call the method under test
        final ResponseEntity<Item> response = itemController.getItemById(firstItemId);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        Item responseItem = response.getBody();
        assertNull(responseItem);
    }

    @Test
    public void getItemsByNameHappyPath() {
        // Test variables
        final String testItemName = "Item1";
        Item testItem = testItems.get(0);
        assert testItem != null;

        // Stub
        when(itemRepository.findByName(testItemName)).thenReturn(testItems.stream().filter(item -> item.getName().equals(testItemName)).collect(Collectors.toList()));

        // Call the method under test
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(testItemName);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(responseItems.size(), 1);
        assertEquals(responseItems.get(0).getId(), testItem.getId());
        assertEquals(responseItems.get(0).getName(), testItem.getName());
        assertEquals(responseItems.get(0).getPrice(), testItem.getPrice());
        assertEquals(responseItems.get(0).getDescription(), testItem.getDescription());
    }

    @Test
    public void getItemsByNameItemNotFound() {
        // Test variables
        final String testItemName = "Item2";

        // Stub
        when(itemRepository.findByName(testItemName)).thenReturn(null);

        // Call the method under test
        final ResponseEntity<List<Item>> response = itemController.getItemsByName(testItemName);

        // Assertions
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        List<Item> responseItems = response.getBody();
        assertNull(responseItems);
    }
}
