package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    private final ShoppingCartDao shoppingCartDao;
    private final ProfileDao profileDao;

    public MySqlOrderDao(DataSource dataSource, ShoppingCartDao shoppingCartDao, ProfileDao profileDao) {
        super(dataSource);
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
    }

    @Override
    public Order createOrder(int userId) {
        ShoppingCart cart = shoppingCartDao.getByUserId(userId);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty())
            throw new RuntimeException("Cart is empty.");

        Profile profile = profileDao.getByUserId(userId);
        if (profile == null) profile = new Profile();

        String insertOrderSql = """
                    INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        // NOTE: some versions of this capstone use price column names differently.
        // We'll detect which price column exists in order_line_items.
        String clearCartSql = "DELETE FROM shopping_cart WHERE user_id = ?";

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            int orderId;

            // 1) Create order row
            try (PreparedStatement ps = connection.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, userId);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(3, safe(profile.getAddress()));
                ps.setString(4, safe(profile.getCity()));
                ps.setString(5, safe(profile.getState()));
                ps.setString(6, safe(profile.getZip()));
                ps.setBigDecimal(7, BigDecimal.ZERO); // shipping_amount (safe default)

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next())
                        throw new RuntimeException("Order ID was not generated.");
                    orderId = keys.getInt(1);
                }
            }

            // 2) Insert line items
            String priceCol = detectPriceColumn(connection);
            String insertLineSql = buildInsertLineSql(priceCol);

            try (PreparedStatement linePs = connection.prepareStatement(insertLineSql)) {
                for (ShoppingCartItem item : cart.getItems().values()) {
                    // order_id, product_id, quantity, <priceCol>
                    linePs.setInt(1, orderId);
                    linePs.setInt(2, item.getProductId());
                    linePs.setInt(3, item.getQuantity());
                    linePs.setBigDecimal(4, item.getProduct().getPrice());
                    linePs.addBatch();
                }
                linePs.executeBatch();
            }

            // 3) Clear cart
            try (PreparedStatement clearPs = connection.prepareStatement(clearCartSql)) {
                clearPs.setInt(1, userId);
                clearPs.executeUpdate();
            }

            connection.commit();

            Order order = new Order();
            order.setOrderId(orderId);
            order.setUserId(userId);
            return order;
        } catch (Exception e) {
            throw new RuntimeException("Create order failed: " + e.getMessage(), e);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    // Detect whether the line-items table uses price / unit_price / sales_price
    private String detectPriceColumn(Connection connection) throws SQLException {
        Set<String> cols = new java.util.HashSet<>();
        DatabaseMetaData meta = connection.getMetaData();

        try (ResultSet rs = meta.getColumns(connection.getCatalog(), null, "order_line_items", null)) {
            while (rs.next())
                cols.add(rs.getString("COLUMN_NAME").toLowerCase());
        }

        if (cols.contains("price")) return "price";
        if (cols.contains("unit_price")) return "unit_price";
        if (cols.contains("sales_price")) return "sales_price";

        // fallback (most common in this project is price)
        return "price";
    }

    private String buildInsertLineSql(String priceCol) {
        return "INSERT INTO order_line_items (order_id, product_id, quantity, " + priceCol + ") VALUES (?, ?, ?, ?)";
    }
}
