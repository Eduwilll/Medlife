package medlife.com.br.helper

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object UsuarioFirebase {
    @JvmStatic
    val idUsuario: String?
        get() {
            val autenticacao: FirebaseAuth = ConfiguracaoFirebase.firebaseAutenticacao
            return autenticacao.currentUser?.uid
        }

    @JvmStatic
    val usuarioAtual: FirebaseUser?
        get() {
            val usuario: FirebaseAuth = ConfiguracaoFirebase.firebaseAutenticacao
            return usuario.currentUser
        }

    /**
     * Update the lastLogin timestamp for the current user
     */
    @JvmStatic
    fun atualizarLastLogin() {
        val currentUser = usuarioAtual
        if (currentUser != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios").document(currentUser.uid)
                .update("lastLogin", Timestamp.now())
                .addOnSuccessListener {
                    println("LastLogin updated successfully for user: ${currentUser.uid}")
                }
                .addOnFailureListener { e ->
                    println("Error updating lastLogin: ${e.message}")
                    e.printStackTrace()
                }
        } else {
            println("No authenticated user found for lastLogin update")
        }
    }

    /**
     * Update the lastLogin timestamp for a specific user
     */
    @JvmStatic
    fun atualizarLastLogin(userId: String?) {
        if (!userId.isNullOrEmpty()) {
            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios").document(userId)
                .update("lastLogin", Timestamp.now())
                .addOnSuccessListener {
                    println("LastLogin updated successfully for user: $userId")
                }
                .addOnFailureListener { e ->
                    println("Error updating lastLogin for user $userId: ${e.message}")
                    e.printStackTrace()
                }
        } else {
            println("Invalid userId for lastLogin update: $userId")
        }
    }

    /**
     * Safely update lastLogin without overwriting other user data
     */
    @JvmStatic
    fun atualizarLastLoginSeguro(userId: String?) {
        if (!userId.isNullOrEmpty()) {
            val db = FirebaseFirestore.getInstance()

            db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        db.collection("usuarios").document(userId)
                            .update("lastLogin", Timestamp.now())
                            .addOnSuccessListener {
                                println("LastLogin updated safely for user: $userId")
                            }
                            .addOnFailureListener { e ->
                                println("Error updating lastLogin safely: ${e.message}")
                            }
                    } else {
                        println("User document doesn't exist for lastLogin update: $userId")
                    }
                }
                .addOnFailureListener { e ->
                    println("Error checking user document for lastLogin update: ${e.message}")
                }
        } else {
            println("Invalid userId for safe lastLogin update: $userId")
        }
    }
}
