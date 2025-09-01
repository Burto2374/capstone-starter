package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.OrderDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Order;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")
public class OrdersController
{
    private final OrderDao orderDao;
    private final ShoppingCartDao shoppingCartDao;
    private final UserDao userDao;

    @Autowired
    public OrdersController(OrderDao orderDao, ShoppingCartDao shoppingCartDao, UserDao userDao) {
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
    }

    @PostMapping
    public ResponseEntity<Order> checkout(Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUsername(username);
        int userId = user.getUserId();

        // 1. Get the user's cart
        ShoppingCart cart = shoppingCartDao.getByUserId(userId);

        // 2. Create the order (implement this in your OrderDao)
        Order order = orderDao.createOrderFromCart(userId, cart);

        // 3. Clear the cart
        shoppingCartDao.clearCart(userId);

        return ResponseEntity.ok(order);
    }
}