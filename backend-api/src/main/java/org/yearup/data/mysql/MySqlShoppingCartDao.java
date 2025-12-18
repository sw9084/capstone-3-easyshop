package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        // Join shopping_cart + products so each cart item has full Product details
        String sql =
                "SELECT " +
                        " sc.product_id, sc.quantity, " +
                        " p.name, p.price, p.category_id, p.description, p.subcategory, p.image_url, p.stock, p.featured " +
                        "FROM shopping_cart sc " +
                        "JOIN products p ON p.product_id = sc.product_id " +
                        "WHERE sc.user_id = ? " +
                        "ORDER BY sc.product_id;";

        ShoppingCart cart = new ShoppingCart();

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet row = ps.executeQuery()) {
                while (row.next()) {
                    // Build Product from DB row
                    Product product = new Product(
                            row.getInt("product_id"),
                            row.getString("name"),
                            row.getBigDecimal("price"),
                            row.getInt("category_id"),
                            row.getString("description"),
                            row.getString("subcategory"),
                            row.getInt("stock"),
                            row.getBoolean("featured"),
                            row.getString("image_url")
                    );

                    // Build ShoppingCartItem using Product + quantity
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(row.getInt("quantity"));
                    item.setDiscountPercent(BigDecimal.ZERO);

                    // ShoppingCart stores items in a Map keyed by productId
                    cart.add(item);

                }
            }


        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving shopping cart.", e);
        }

        return cart;
    }

    @Override
    public void addProduct(int userId, int productId) {
        // If (user_id, product_id) is UNIQUE, this will increment quantity instead of duplicating rows
        String sql =
                "INSERT INTO shopping_cart (user_id, product_id, quantity) " +
                        "VALUES (?, ?, 1) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + 1;";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding product to cart.", e);
        }
    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity) {
        // If quantity is 0, remove the item from the cart
        if (quantity == 0) {
            String deleteSql = "DELETE FROM shopping_cart WHERE user_id = ? AND product_id = ?;";

            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement(deleteSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, productId);
                ps.executeUpdate();
                return;
            } catch (SQLException e) {
                throw new RuntimeException("Error removing cart item.", e);
            }
        }

        // Otherwise update quantity normally
        String sql =
                "UPDATE shopping_cart " +
                        "SET quantity = ? " +
                        "WHERE user_id = ? AND product_id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating cart quantity.", e);
        }
    }

    @Override
    public void clearCart(int userId) {
        // Remove all items from the user's cart
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error clearing shopping cart.", e);
        }
    }
}

