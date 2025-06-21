package medlife.com.br.model;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Map;

public class Order {
    private String orderId;
    private String userId;
    private List<Map<String, Object>> items;
    private double totalPrice;
    private String status;
    private Timestamp createdAt;

    public Order() {
        // Required empty public constructor
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<Map<String, Object>> getItems() { return items; }
    public void setItems(List<Map<String, Object>> items) { this.items = items; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
} 