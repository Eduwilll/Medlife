package medlife.com.br.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.helper.UsuarioFirebase

class ProfileWalletFragment : Fragment() {
    private lateinit var textSaldo: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_wallet, container, false)

        db = FirebaseFirestore.getInstance()

        textSaldo = view.findViewById(R.id.textSaldo)

        loadWalletData()

        return view
    }

    private fun loadWalletData() {
        val userId = UsuarioFirebase.idUsuario
        if (userId != null) {
            db.collection("carteiras")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val saldo = documentSnapshot.getDouble("saldo")
                        if (saldo != null) {
                            textSaldo.text = String.format("R$ %.2f", saldo)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }
}
