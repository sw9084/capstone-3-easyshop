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
    public OrdersController(UserDao userDao, OrderDao orderDao) {
        this.userDao = userDao;
        this.orderDao = orderDao;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int createOrder( @RequestBody Order order, Principal principal) {
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        return orderDao.createOrder(user.getId(), order);
    }
}
