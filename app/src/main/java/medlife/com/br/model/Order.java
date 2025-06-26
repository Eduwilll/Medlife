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
    
    // Delivery Information
    private String deliveryOption; // "immediate", "pickup", "scheduled"
    private double deliveryFee;
    private Map<String, Object> deliveryAddress;
    
    // Payment Information
    private String paymentMethod;
    private String paymentStatus; // "pending", "paid", "failed"
    
    // Summary Breakdown
    private double subtotal;
    private double discountAmount;
    private String couponCode;
    
    // Store Information
    private String storeName;
    private String storeLocation;
    
    // Additional Information
    private String notes;
    private Timestamp estimatedDeliveryTime;
    private boolean requiresPrescription;

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
    
    // Delivery Information
    public String getDeliveryOption() { return deliveryOption; }
    public void setDeliveryOption(String deliveryOption) { this.deliveryOption = deliveryOption; }
    
    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    
    public Map<String, Object> getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(Map<String, Object> deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    // Payment Information
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    // Summary Breakdown
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    
    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    
    // Store Information
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    
    public String getStoreLocation() { return storeLocation; }
    public void setStoreLocation(String storeLocation) { this.storeLocation = storeLocation; }
    
    // Additional Information
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public Timestamp getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(Timestamp estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    
    public boolean isRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }
} 