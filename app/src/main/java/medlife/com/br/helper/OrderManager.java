package medlife.com.br.helper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.model.Order;
import medlife.com.br.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class OrderManager {
    public static Task<Void> saveOrder(Order order) {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId == null) {
            return null;
        }
        
        FirebaseFirestore db = ConfiguracaoFirebase.getFirestore();
        
        // Convert order to Map for simpler Firestore operations
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("orderId", order.getOrderId());
        orderMap.put("userId", order.getUserId());
        orderMap.put("items", order.getItems());
        orderMap.put("totalPrice", order.getTotalPrice());
        orderMap.put("status", order.getStatus());
        orderMap.put("createdAt", order.getCreatedAt());
        
        // Use arrayUnion to add the order to the user's orders array
        return db.collection("usuarios").document(userId)
                .update("orders", com.google.firebase.firestore.FieldValue.arrayUnion(orderMap));
    }
} 