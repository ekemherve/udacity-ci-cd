package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private static final String USERNAME = "herve";
    private final static String HASHED_PASSWORD = "hashedHervePaswword";

    private static final String ITEM_NAME_ROUND_WIDGET = "Round Widget";
    private static final String ROUND_WIDGET_DESCRIPTION = "A widget that is round";

    @Before
    public void before() {

        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void submitTest() {

        when(userRepository.findByUsername(USERNAME)).thenReturn(getUser());

        when(orderRepository.save(any())).thenReturn(getUserOrder());

        ResponseEntity<UserOrder> responseEntity = orderController.submit(USERNAME);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(USERNAME, Objects.requireNonNull(responseEntity.getBody()).getUser().getUsername());
    }

    @Test
    public void submitWhenUserNotExistReturnNotFoundExceptionTest() {

        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        when(orderRepository.save(any())).thenReturn(getUserOrder());

        ResponseEntity<UserOrder> responseEntity = orderController.submit(USERNAME);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
    public void ordersForUserTest() {

        User user = getUser();
        when(userRepository.findByUsername(USERNAME)).thenReturn(getUser());

        UserOrder order = getUserOrder();
        order.setUser(user);
        when(orderRepository.findByUser(any())).thenReturn(Collections.singletonList(order));

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(USERNAME);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(USERNAME, Objects.requireNonNull(responseEntity.getBody()).get(0).getUser().getUsername());
    }

    @Test
    public void ordersForUserWhenUserNotExistsReturnNotFoundTest() {

        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        when(orderRepository.findByUser(any())).thenReturn(Collections.singletonList(getUserOrder()));

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser(USERNAME);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    private User getUser() {
        User user = getUserWithEmptyCar();
        user.setCart(getCart());
        user.getCart().setUser(user);
        return user;
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

    private Item getItem() {

        Item item = new Item();
        item.setId(1L);
        item.setName(ITEM_NAME_ROUND_WIDGET);
        item.setDescription(ROUND_WIDGET_DESCRIPTION);
        item.setPrice(BigDecimal.valueOf(2.99));
        return item;
    }

    private UserOrder getUserOrder() {
        UserOrder order = new UserOrder();
        order.setId(1L);
        List<Item> items = new ArrayList<>();
        items.add(getItem());
        order.setItems(items);
        order.setTotal(items.stream().map(Item::getPrice).reduce(BigDecimal::add).get());
        return order;
    }


}
