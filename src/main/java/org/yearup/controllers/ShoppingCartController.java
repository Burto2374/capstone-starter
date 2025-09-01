package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("/cart")
@PreAuthorize("isAuthenticated()")
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    @SuppressWarnings("unused")
    private ProductDao productDao;

    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        String username = principal.getName();
        var user = userDao.getByUsername(username);
        int userId = user.getUserId();

        return shoppingCartDao.getByUserId(userId);
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("/products/{productId}")
    public ResponseEntity<Void> addProductToCart(@PathVariable int productId, Principal principal)
    {
        String username = principal.getName();
        var user = userDao.getByUsername(username);
        int userId = user.getUserId();

        shoppingCartDao.addProduct(userId, productId, 1); // Adds 1 or increments if already present

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/products/{productId}")
    public ResponseEntity<Void> updateProductQuantity(
            @PathVariable int productId,
            @RequestBody ShoppingCartItem item,
            Principal principal)
    {
        String username = principal.getName();
        User user = userDao.getByUsername(username);
        int userId = user.getUserId();

        shoppingCartDao.updateProductQuantity(userId, productId, item.getQuantity());

        return ResponseEntity.noContent().build();
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Principal principal)
    {
        String username = principal.getName();
        var user = userDao.getByUsername(username);
        int userId = user.getUserId();

        shoppingCartDao.clearCart(userId);

        return ResponseEntity.noContent().build();
    }

}
