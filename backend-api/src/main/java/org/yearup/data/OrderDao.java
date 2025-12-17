package org.yearup.data;

import org.yearup.models.Order;


public interface OrderDao {
    int createOrder(int userId,Order order );
}
