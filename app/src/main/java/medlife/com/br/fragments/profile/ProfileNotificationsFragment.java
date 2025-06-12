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

public class ProfileNotificationsFragment extends Fragment {
    private RecyclerView recyclerNotifications;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_notifications, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerNotifications = view.findViewById(R.id.recyclerNotifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load notifications
        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        String userId = UsuarioFirebase.getIdUsuario();
        db.collection("notificacoes")
                .whereEqualTo("userId", userId)
                .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // TODO: Implement notification adapter and display notifications
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
} 