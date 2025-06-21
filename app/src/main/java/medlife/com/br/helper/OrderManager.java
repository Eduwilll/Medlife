package medlife.com.br.helper;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
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
        
        // Save the Order object directly
        return db.collection("usuarios").document(userId)
                .update("orders", com.google.firebase.firestore.FieldValue.arrayUnion(order));
    }
} 