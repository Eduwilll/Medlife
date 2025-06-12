package medlife.com.br.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;

public class ProfileWalletFragment extends Fragment {
    private TextView textSaldo;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_wallet, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        textSaldo = view.findViewById(R.id.textSaldo);

        // Load wallet data
        loadWalletData();

        return view;
    }

    private void loadWalletData() {
        String userId = UsuarioFirebase.getIdUsuario();
        db.collection("carteiras")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double saldo = documentSnapshot.getDouble("saldo");
                        if (saldo != null) {
                            textSaldo.setText(String.format("R$ %.2f", saldo));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
} 