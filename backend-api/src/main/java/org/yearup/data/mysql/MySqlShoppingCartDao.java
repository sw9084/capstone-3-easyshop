package org.yearup.data.mysql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    @Autowired
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public ShoppingCart getByUserId(int userId) {
        return new ShoppingCart();
    }
    @Override
    public void addProductToCart(int userId, int productId) {
        String selectSql = """
                SELECT quantity
                FROM shopping_cart
                WHERE user_id = ? AND product_id = ?""";
        String insertSql = """
                UPDATE INTO shopping_cart (user_id, product_id, quantity)
                 VALUE (?, ?, 1)""";
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
    public void clearCart(int userId) {

    }
}
