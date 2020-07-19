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
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {


    private static final Long ITEM_ID = 1L;
    private static final int ITEM_QUANTITY = 1;
    private static final String ITEM_NAME_ROUND_WIDGET = "Round Widget";
    private static final String ROUND_WIDGET_DESCRIPTION = "A widget that is round";

    private static final String USERNAME = "herve";
    private final static String HASHED_PASSWORD = "hashedHervePaswword";


    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);


    @Before
    public void before() {

        cartController = new CartController();
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
    }

    @Test
    public void addToCart() {

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        User user = getUserWithEmptyCar();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.ofNullable(getItem()));

        Cart cart = user.getCart();

        when(cartRepository.save(cart)).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(USERNAME, Objects.requireNonNull(responseEntity.getBody()).getUser().getUsername());

    }

    @Test
    public void addToCartWithNonExistingUserReturnNotFoundStatusTest() {

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        User user = getUserWithEmptyCar();

        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.ofNullable(getItem()));

        Cart cart = user.getCart();

        when(cartRepository.save(cart)).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void addToCartWithNonExistingItemReturnNotFoundStatusTest() {

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        User user = getUserWithEmptyCar();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        Cart cart = user.getCart();

        when(cartRepository.save(cart)).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCart() {

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        User user = getUser();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.of(getItem()));

        Cart cart = user.getCart();

        when(cartRepository.save(cart)).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(USERNAME, Objects.requireNonNull(responseEntity.getBody()).getUser().getUsername());

    }

    @Test
    public void removeFromCartWithNonExistingUserReturnNotFoundStatusTest() {

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        User user = getUserWithEmptyCar();

        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.ofNullable(getItem()));

        Cart cart = user.getCart();

        when(cartRepository.save(cart)).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void removeFromCartWithNonExistingItemReturnNotFoundStatusTest() {

        ModifyCartRequest modifyCartRequest = getModifyCartRequest();
        User user = getUserWithEmptyCar();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);
        when(itemRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        Cart cart = user.getCart();

        when(cartRepository.save(cart)).thenReturn(cart);

        ResponseEntity<Cart> responseEntity = cartController.removeFromcart(modifyCartRequest);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    private ModifyCartRequest getModifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(ITEM_ID);
        modifyCartRequest.setQuantity(ITEM_QUANTITY);
        modifyCartRequest.setUsername(USERNAME);
        return modifyCartRequest;
    }

    private User getUserWithEmptyCar() {
        User user = new User();
        user.setId(1L);
        user.setUsername(USERNAME);
        user.setPassword(HASHED_PASSWORD);
        user.setCart(getEmptyCart());
        user.getCart().setUser(user);
        return user;
    }

    private User getUser() {
        User user = getUserWithEmptyCar();
        user.setCart(getCart());
        user.getCart().setUser(user);
        return user;
    }

    private Item getItem() {

        Item item = new Item();
        item.setId(1L);
        item.setName(ITEM_NAME_ROUND_WIDGET);
        item.setDescription(ROUND_WIDGET_DESCRIPTION);
        item.setPrice(BigDecimal.valueOf(2.99));
        return item;
    }

    private Cart getEmptyCart() {

        Cart cart = new Cart();
        cart.setId(1L);
        return cart;
    }

    private Cart getCart() {

        Cart cart = getEmptyCart();
        List<Item> items = new ArrayList<>();
        items.add(getItem());
        cart.setItems(items);
        cart.setTotal(cart.getItems().stream().map(Item::getPrice).reduce(BigDecimal::add).get());
        return cart;
    }

}
