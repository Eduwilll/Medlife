package medlife.com.br.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.helper.UsuarioFirebase

class ProfileCouponsFragment : Fragment() {
    private lateinit var recyclerCoupons: RecyclerView
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_coupons, container, false)

        db = FirebaseFirestore.getInstance()

        recyclerCoupons = view.findViewById(R.id.recyclerCoupons)
        recyclerCoupons.layoutManager = LinearLayoutManager(context)

        loadCoupons()

        return view
    }

    private fun loadCoupons() {
        val userId = UsuarioFirebase.idUsuario
        if (userId != null) {
            db.collection("cupons")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { queryDocumentSnapshots ->
                    // TODO: Implement coupon adapter and display coupons
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }
    }
}
