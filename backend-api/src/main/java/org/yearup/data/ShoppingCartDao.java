package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao {
    // get the shopping cart for a specific user
    ShoppingCart getByUserId(int userId);

    // add a product to the cart (increase quantity if it already exists)
    void addProduct(int userId, int productId);

    // update the quantity of a product already in the cart
    void updateQuantity(int userId, int productId, int quantity);

    // remove all products from the user's cart
    void clearCart(int userId);
}