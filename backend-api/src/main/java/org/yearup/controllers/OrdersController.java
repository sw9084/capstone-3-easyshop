package org.yearup.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.User;
import org.yearup.models.Order;

import java.security.Principal;

@RestController
@RequestMapping("/orders")

public class OrdersController {
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final ShoppingCartDao shoppingCartDao;

    public OrdersController(UserDao userDao, OrderDao orderDao, ShoppingCartDao shoppingCartDao) {
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int createOrder(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        Order oder = new Order();

        int orderId = orderDao.createOrder(user.getId(), oder);

        shoppingCartDao.clearCart(user.getId());
        return orderId;
    }
}
