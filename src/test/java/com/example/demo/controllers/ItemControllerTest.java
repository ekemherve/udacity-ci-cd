package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private static final String ROUND_WIDGET = "Round Widget";
    private static final String ROUND_WIDGET_DESCRIPTION = "A widget that is round";

    @Before
    public void before() {

        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsTest() {

        when(itemRepository.findAll()).thenReturn(Collections.singletonList(getItem()));
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
    }

    @Test
    public void getItemByIdTest() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(getItem()));
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(ROUND_WIDGET_DESCRIPTION, Objects.requireNonNull(responseEntity.getBody()).getDescription());
    }

    @Test
    public void getItemByNameTest() {

        when(itemRepository.findByName(ROUND_WIDGET)).thenReturn(Collections.singletonList(getItem()));
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(ROUND_WIDGET);
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());
        assertEquals(ROUND_WIDGET_DESCRIPTION, Objects.requireNonNull(responseEntity.getBody().get(0).getDescription()));
    }

    @Test
    public void getItemByNameIsEmptyTest() {

        when(itemRepository.findByName(ROUND_WIDGET)).thenReturn(Collections.EMPTY_LIST);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(ROUND_WIDGET);
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void getItemByNameIsNullTest() {

        when(itemRepository.findByName(ROUND_WIDGET)).thenReturn(null);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName(ROUND_WIDGET);
        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    private Item getItem() {

        Item item = new Item();
        item.setId(1L);
        item.setDescription(ROUND_WIDGET_DESCRIPTION);
        item.setName(ROUND_WIDGET);
        item.setPrice(BigDecimal.valueOf(2.99));
        return item;
    }

}
