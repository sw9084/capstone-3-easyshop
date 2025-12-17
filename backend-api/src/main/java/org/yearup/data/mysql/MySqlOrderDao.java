package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;

@Component

public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public int createOrder(int userId, Order order) {
        String insertOrderSql = """
                INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                VALUES (?, ?, ?, ?, ?, ?, ?)""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     insertOrderSql,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(3, order.getAddress());
            preparedStatement.setString(4, order.getCity());
            preparedStatement.setString(5,order.getState());
            preparedStatement.setString(6, order.getZip());
            preparedStatement.setDouble(7, order.getShippingAmount());
            preparedStatement.executeUpdate();

            ResultSet Keys = preparedStatement.getGeneratedKeys();
            if (!Keys.next()) {
                throw new RuntimeException("Failed to create order");
            }
            int orderId = Keys.getInt(1);

            String insertItemsSql = """
                    INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                    SELECT ?, sc.product_id, p.price, sc.quantity, 0
                    FROM shopping_cart sc
                    JOIN products p ON sc.product_id = p.product_id
                    WHERE sc.user_id = ?""";
            try (PreparedStatement itemPs = connection.prepareStatement(insertItemsSql)) {
                itemPs.setInt(1, orderId);
                itemPs.setInt(2, userId);
                itemPs.executeUpdate();
            }
            String clearCartSql = "DELETE FROM shopping_cart WHERE user_id = ?";
            try (PreparedStatement clearPs = connection.prepareStatement(clearCartSql)) {
                clearPs.setInt(1, userId);
                clearPs.executeUpdate();
            }
            return orderId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
