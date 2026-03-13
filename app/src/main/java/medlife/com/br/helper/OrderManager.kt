package medlife.com.br.helper

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import medlife.com.br.model.Order
import java.util.UUID

object OrderManager {

    fun saveOrder(order: Order): Task<Void>? {
        val userId = UsuarioFirebase.getIdUsuario() ?: return null
        val db: FirebaseFirestore = ConfiguracaoFirebase.getFirestore()

        if (order.orderId.isNullOrEmpty()) {
            order.orderId = UUID.randomUUID().toString()
        }

        order.createdAt = Timestamp.now()
        order.userId = userId

        // Save to orders collection
        val saveToOrders: Task<Void> = db.collection("orders")
            .document(order.orderId!!)
            .set(order)

        // Also save to user's orders array for quick access
        db.collection("usuarios").document(userId)
            .update("orders", com.google.firebase.firestore.FieldValue.arrayUnion(order))

        return saveToOrders
    }

    fun getUserOrders(userId: String): Task<QuerySnapshot> {
        val db: FirebaseFirestore = ConfiguracaoFirebase.getFirestore()
        return db.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
    }

    fun getAllOrders(): Task<QuerySnapshot> {
        val db: FirebaseFirestore = ConfiguracaoFirebase.getFirestore()
        return db.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
    }

    fun updateOrderStatus(orderId: String, newStatus: String): Task<Void> {
        val db: FirebaseFirestore = ConfiguracaoFirebase.getFirestore()
        return db.collection("orders")
            .document(orderId)
            .update("status", newStatus)
    }

    fun updatePaymentStatus(orderId: String, paymentStatus: String): Task<Void> {
        val db: FirebaseFirestore = ConfiguracaoFirebase.getFirestore()
        return db.collection("orders")
            .document(orderId)
            .update("paymentStatus", paymentStatus)
    }
}
