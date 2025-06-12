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

public class ProfileAboutFragment extends Fragment {
    private TextView textVersion, textAboutContent;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_about, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        textVersion = view.findViewById(R.id.textVersion);
        textAboutContent = view.findViewById(R.id.textAboutContent);

        // Set app version
        try {
            String versionName = getContext().getPackageManager()
                    .getPackageInfo(getContext().getPackageName(), 0).versionName;
            textVersion.setText("Versão " + versionName);
        } catch (Exception e) {
            textVersion.setText("Versão não disponível");
        }

        // Load about content
        loadAboutContent();

        return view;
    }

    private void loadAboutContent() {
        db.collection("conteudo")
                .document("sobre")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String conteudo = documentSnapshot.getString("conteudo");
                        if (conteudo != null) {
                            textAboutContent.setText(conteudo);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
} 