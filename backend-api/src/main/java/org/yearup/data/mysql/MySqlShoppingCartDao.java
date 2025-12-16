package org.yearup.data.mysql;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    private final ProductDao productDao;
    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }
    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        Map<Integer, ShoppingCartItem> items = new HashMap<>();

        String sql = """
                SELECT sc.product_id, sc.quantity, p.price
                FROM shopping_cart sc
                JOIN products p on sc.product_id = p.product_id
                WHERE sc.user_id = ?""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                Product product = productDao.getById(resultSet.getInt("product_id"));
                item.setProduct(product);
                item.setQuantity(resultSet.getInt("quantity"));

                items.put(product.getProductId(), item);

            }
            cart.setItems(items);
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        return cart;
    }
    @Override
    public void addProductToCart(int userId, int productId) {
        String selectSql = """
                SELECT quantity
                FROM shopping_cart
                WHERE user_id = ? AND product_id = ?""";
        String insertSql = """
                INSERT INTO shopping_cart (user_id, product_id, quantity)
                VALUES (?, ?, 1)""";
        String updateSql = """
                UPDATE shopping_cart
                SET quantity = quantity + 1
                WHERE user_id = ?
                AND product_id =?""";
        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement =
                    connection.prepareStatement(selectSql);
            selectStatement.setInt(1, userId);
            selectStatement.setInt(2, productId);

            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                PreparedStatement updateStatement =
                        connection.prepareStatement(updateSql);
                updateStatement.setInt(1, userId);
                updateStatement.setInt(2, productId);
                updateStatement.executeUpdate();
            } else {
                PreparedStatement insertStatement =
                        connection.prepareStatement(insertSql);
                insertStatement.setInt(1, userId);
                insertStatement.setInt(2, productId);
                insertStatement.executeUpdate();
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    @Override
    public void updateProductQuantity(int userId,int productId, int quantity) {

    }
    @Override
    public ShoppingCartItem getItemByUserAndProduct(int userId, int productId) {
        String sql = """
                SELECT user_id, product_id, quantity
                FROM shopping_cart
                WHERE user_id = ?
                AND products_id = ?""";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                ShoppingCartItem item = new ShoppingCartItem();
                item.setQuantity(resultSet.getInt("quantity"));
                return item;

            }
            return null;

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearCart(int userId) {

    }
}
