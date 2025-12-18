package org.yearup.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;
import org.yearup.models.Order;

import java.security.Principal;

@RestController
@RequestMapping("/orders")

public class OrdersController {
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final ShoppingCartDao shoppingCartDao;
    private final ProfileDao profileDao;

    public OrdersController(UserDao userDao, OrderDao orderDao, ShoppingCartDao shoppingCartDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        Profile profile = profileDao.getByUserId(user.getId());
        Order order = new Order();
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());

        int orderId = orderDao.createOrder(user.getId(), order);


        shoppingCartDao.clearCart(user.getId());
        return order;
    }
}
