package medlife.com.br.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import medlife.com.br.R
import medlife.com.br.helper.UsuarioFirebase

class ProfileNotificationsFragment : Fragment() {
    private lateinit var recyclerNotifications: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_notifications, container, false)

        db = FirebaseFirestore.getInstance()

        recyclerNotifications = view.findViewById(R.id.recyclerNotifications)
        recyclerNotifications.layoutManager = LinearLayoutManager(context)

        loadNotifications()

        return view
    }

    private fun loadNotifications() {
        val userId = UsuarioFirebase.idUsuario
        if (userId != null) {
            db.collection("notificacoes")
                .whereEqualTo("userId", userId)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    // TODO: Implement notification adapter and display notifications
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }
}
