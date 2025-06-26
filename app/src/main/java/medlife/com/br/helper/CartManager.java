package medlife.com.br.helper;

import java.util.ArrayList;
import java.util.List;
import medlife.com.br.model.CartItem;
import medlife.com.br.model.Product;
import medlife.com.br.model.Order;
import medlife.com.br.model.OrderStatus;
import java.util.HashMap;
import java.util.Map;

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

    public Order createOrderFromCart() {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId == null || cartItems.isEmpty()) {
            return null;
        }

        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("productName", item.getProduct().getName());
            orderItem.put("quantity", item.getQuantity());
            orderItem.put("price", item.getProduct().getPrice());
            orderItem.put("totalItemPrice", Double.parseDouble(item.getProduct().getPrice().replace("R$", "").replace(",", ".").trim()) * item.getQuantity());
            orderItem.put("category", item.getProduct().getCategory());
            orderItem.put("brand", item.getProduct().getBrand());
            orderItem.put("tarja", item.getProduct().getTarja());
            orderItem.put("description", item.getProduct().getDescription());
            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setTotalPrice(getTotalPrice());
        order.setStatus(OrderStatus.ORDER_CONFIRMED.toString());
        
        // Set subtotal
        order.setSubtotal(getTotalPrice());
        
        // Set default values for other fields
        // These will be updated by CartFragment before saving
        order.setDeliveryOption("immediate");
        order.setDeliveryFee(7.00);
        order.setDiscountAmount(0.0);
        order.setPaymentMethod("Pendente");
        order.setPaymentStatus("pending");
        order.setStoreName("Drogaria São Paulo");
        order.setStoreLocation("Cidade Campinas");
        order.setRequiresPrescription(false);

        return order;
    }
} 