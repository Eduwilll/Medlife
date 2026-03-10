package medlife.com.br.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object ConfiguracaoFirebase {
    private var referenciaFirebase: DatabaseReference? = null
    private var referenciaAutenticacao: FirebaseAuth? = null
    private var referenciaStorage: StorageReference? = null
    private var referenciaFirestore: FirebaseFirestore? = null

    // retorna a referencia do database
    @JvmStatic
    val firebaseDatabase: DatabaseReference
        get() {
            if (referenciaFirebase == null) {
                referenciaFirebase = FirebaseDatabase.getInstance().reference
            }
            return referenciaFirebase!!
        }

    // retorna a instancia do FirebaseAuth
    @JvmStatic
    val firebaseAutenticacao: FirebaseAuth
        get() {
            if (referenciaAutenticacao == null) {
                referenciaAutenticacao = FirebaseAuth.getInstance()
            }
            return referenciaAutenticacao!!
        }

    @JvmStatic
    val firestore: FirebaseFirestore
        get() {
            if (referenciaFirestore == null) {
                referenciaFirestore = FirebaseFirestore.getInstance()
            }
            return referenciaFirestore!!
        }

    // Retorna instancia do FirebaseStorage
    @JvmStatic
    val firebaseStorage: StorageReference
        get() {
            if (referenciaStorage == null) {
                referenciaStorage = FirebaseStorage.getInstance().reference
            }
            return referenciaStorage!!
        }
}
