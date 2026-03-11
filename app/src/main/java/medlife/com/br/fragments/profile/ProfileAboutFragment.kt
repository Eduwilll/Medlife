package medlife.com.br.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R

class ProfileAboutFragment : Fragment() {
    private lateinit var textVersion: TextView
    private lateinit var textAboutContent: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_about, container, false)

        db = FirebaseFirestore.getInstance()

        textVersion = view.findViewById(R.id.textVersion)
        textAboutContent = view.findViewById(R.id.textAboutContent)

        try {
            val versionName = requireContext().packageManager
                .getPackageInfo(requireContext().packageName, 0).versionName
            textVersion.text = "Versão $versionName"
        } catch (e: Exception) {
            textVersion.text = "Versão não disponível"
        }

        loadAboutContent()

        return view
    }

    private fun loadAboutContent() {
        db.collection("conteudo")
            .document("sobre")
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val conteudo = documentSnapshot.getString("conteudo")
                    if (conteudo != null) {
                        textAboutContent.text = conteudo
                    }
                }
            }
            .addOnFailureListener {
                // Handle error
            }
    }
}
