package medlife.com.br.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R

class ProfileHelpFragment : Fragment() {
    private lateinit var textHelpContent: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_help, container, false)

        db = FirebaseFirestore.getInstance()

        textHelpContent = view.findViewById(R.id.textHelpContent)

        loadHelpContent()

        return view
    }

    private fun loadHelpContent() {
        db.collection("conteudo")
            .document("ajuda")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val conteudo = documentSnapshot.getString("conteudo")
                    if (conteudo != null) {
                        textHelpContent.text = conteudo
                    }
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}
