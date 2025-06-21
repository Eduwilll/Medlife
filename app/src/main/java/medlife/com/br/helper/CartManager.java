package medlife.com.br.helper;

import java.util.ArrayList;
import java.util.List;
import medlife.com.br.model.CartItem;
import medlife.com.br.model.Product;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addProduct(Product product, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        cartItems.add(new CartItem(product, quantity));
    }

    public void removeProduct(CartItem cartItem) {
        cartItems.remove(cartItem);
    }

    public void updateQuantity(CartItem cartItem, int newQuantity) {
        if (newQuantity > 0) {
            cartItem.setQuantity(newQuantity);
        } else {
            removeProduct(cartItem);
        }
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            String priceString = item.getProduct().getPrice().replace("R$", "").replace(",", ".").trim();
            try {
                total += Double.parseDouble(priceString) * item.getQuantity();
            } catch (NumberFormatException e) {
                // Ignore products with invalid price formats
            }
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }
} 