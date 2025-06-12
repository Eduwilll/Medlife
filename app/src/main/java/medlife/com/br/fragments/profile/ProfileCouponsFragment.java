package medlife.com.br.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;

public class ProfileCouponsFragment extends Fragment {
    private RecyclerView recyclerCoupons;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_coupons, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerCoupons = view.findViewById(R.id.recyclerCoupons);
        recyclerCoupons.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load coupons
        loadCoupons();

        return view;
    }

    private void loadCoupons() {
        String userId = UsuarioFirebase.getIdUsuario();
        db.collection("cupons")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // TODO: Implement coupon adapter and display coupons
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
} 