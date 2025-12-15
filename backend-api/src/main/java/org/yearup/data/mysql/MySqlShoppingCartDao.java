package org.yearup.data.mysql;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;

public class MySqlShoppingCartDao implements ShoppingCartDao {
    @Override
    public ShoppingCart getByUserId(int userId) {
        return null;
    }
    @Override
    public void addProductToCart(int userId, int productId) {

    }
    @Override
    public void updateProductQuantity(int userId,int productId, int quantity) {

    }

    @Override
    public void clearCart(int userId) {

    }
}
