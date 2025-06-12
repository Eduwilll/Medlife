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

public class ProfileHelpFragment extends Fragment {
    private TextView textHelpContent;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_help, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        textHelpContent = view.findViewById(R.id.textHelpContent);

        // Load help content
        loadHelpContent();

        return view;
    }

    private void loadHelpContent() {
        db.collection("conteudo")
                .document("ajuda")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String conteudo = documentSnapshot.getString("conteudo");
                        if (conteudo != null) {
                            textHelpContent.setText(conteudo);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
} 