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

public class ProfileOrdersFragment extends Fragment {
    private RecyclerView recyclerOrders;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_orders, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load orders
        loadOrders();

        return view;
    }

    private void loadOrders() {
        String userId = UsuarioFirebase.getIdUsuario();
        db.collection("pedidos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // TODO: Implement order adapter and display orders
                })
                .addOnFailureListener(e -> {
                    // TODO: Handle error
                });
    }
} 