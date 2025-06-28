package medlife.com.br.helper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import medlife.com.br.model.Order;
import medlife.com.br.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderManager {
    public static Task<Void> saveOrder(Order order) {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId == null) {
            return null;
        }
        
        FirebaseFirestore db = ConfiguracaoFirebase.getFirestore();
        
        // Generate a unique order ID if not already set
        if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
            order.setOrderId(UUID.randomUUID().toString());
        }
        
        // Set the current timestamp manually
        order.setCreatedAt(Timestamp.now());
        
        // Ensure userId is set in the order
        order.setUserId(userId);
        
        // Save to orders collection
        Task<Void> saveToOrders = db.collection("orders")
                .document(order.getOrderId())
                .set(order);
        
        // Also save to user's orders array for quick access
        Task<Void> saveToUser = db.collection("usuarios").document(userId)
                .update("orders", com.google.firebase.firestore.FieldValue.arrayUnion(order));
        
        // Return the orders collection save task
        return saveToOrders;
    }
    
    public static Task<QuerySnapshot> getUserOrders(String userId) {
        FirebaseFirestore db = ConfiguracaoFirebase.getFirestore();
        return db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get();
    }
    
    public static Task<QuerySnapshot> getAllOrders() {
        FirebaseFirestore db = ConfiguracaoFirebase.getFirestore();
        return db.collection("orders")
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get();
    }
    
    public static Task<Void> updateOrderStatus(String orderId, String newStatus) {
        FirebaseFirestore db = ConfiguracaoFirebase.getFirestore();
        return db.collection("orders")
                .document(orderId)
                .update("status", newStatus);
    }
    
    public static Task<Void> updatePaymentStatus(String orderId, String paymentStatus) {
        FirebaseFirestore db = ConfiguracaoFirebase.getFirestore();
        return db.collection("orders")
                .document(orderId)
                .update("paymentStatus", paymentStatus);
    }
} 