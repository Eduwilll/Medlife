package medlife.com.br.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import medlife.com.br.helper.ConfiguracaoFirebase
import medlife.com.br.model.Usuario

class AuthRepository {

    private val auth: FirebaseAuth by lazy { ConfiguracaoFirebase.firebaseAutenticacao }
    private val db: FirebaseFirestore by lazy { ConfiguracaoFirebase.firestore }

    fun getCurrentUser() = auth.currentUser

    suspend fun signInWithEmailAndPassword(email: String, senha: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<Boolean> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            val user = auth.currentUser
            if (user != null) {
                verificarESalvarUsuarioFirestore(user)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerUser(email: String, senha: String, usuario: Usuario): Result<Boolean> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                usuario.uid = firebaseUser.uid
                usuario.lastLogin = com.google.firebase.Timestamp.now()
                // Save user in Firestore
                db.collection("usuarios").document(firebaseUser.uid).set(usuario).await()
                Result.success(true)
            } else {
                Result.failure(Exception("Usuário não criado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verificarESalvarUsuarioFirestore(firebaseUser: com.google.firebase.auth.FirebaseUser) {
        try {
            val document = db.collection("usuarios").document(firebaseUser.uid).get().await()
            if (document.exists()) {
                db.collection("usuarios").document(firebaseUser.uid)
                    .update("lastLogin", com.google.firebase.Timestamp.now()).await()
            } else {
                val usuario = Usuario().apply {
                    uid = firebaseUser.uid
                    email = firebaseUser.email
                    nome = if (!firebaseUser.displayName.isNullOrEmpty()) firebaseUser.displayName else firebaseUser.email
                    lastLogin = com.google.firebase.Timestamp.now()
                }
                db.collection("usuarios").document(firebaseUser.uid).set(usuario).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
