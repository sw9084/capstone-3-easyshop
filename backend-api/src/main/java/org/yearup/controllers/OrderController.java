package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.UserDao;
import org.yearup.models.Order;
import org.yearup.models.User;

import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/orders")
@PreAuthorize("isAuthenticated()")

public class OrderController {

    private final OrderDao orderDao;

    private final UserDao userDao;

    public OrderController(OrderDao orderDao, UserDao userDao) {

        this.orderDao = orderDao;

        this.userDao = userDao;

    }

    @PostMapping

    @ResponseStatus(HttpStatus.CREATED)

    public Order createOrder(Principal principal) {

        if (principal == null)

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        String username = principal.getName();

        User user = userDao.getByUserName(username);

        if (user == null)

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        try {

            return orderDao.createOrder(user.getId());

        } catch (ResponseStatusException e) {

            throw e;

        } catch (Exception e) {

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        }

    }

}


