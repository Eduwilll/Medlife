package medlife.com.br.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.model.Usuario;

public class ProfileUserDataFragment extends Fragment {
    private EditText editNome, editEmail, editGenero;
    private Button buttonSalvar;
    private FirebaseFirestore db;
    private Usuario usuarioAtual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_user_data, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editNome = view.findViewById(R.id.editNome);
        editEmail = view.findViewById(R.id.editEmail);
        editGenero = view.findViewById(R.id.editGenero);
        buttonSalvar = view.findViewById(R.id.buttonSalvar);

        // Load user data
        loadUserData();

        // Set save button click listener
        buttonSalvar.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void loadUserData() {
        String userId = UsuarioFirebase.getIdUsuario();
        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usuarioAtual = documentSnapshot.toObject(Usuario.class);
                        if (usuarioAtual != null) {
                            editNome.setText(usuarioAtual.getNome());
                            editEmail.setText(usuarioAtual.getEmail());
                            editGenero.setText(usuarioAtual.getGenero());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserData() {
        if (usuarioAtual != null) {
            usuarioAtual.setNome(editNome.getText().toString());
            usuarioAtual.setGenero(editGenero.getText().toString());

            db.collection("usuarios")
                    .document(usuarioAtual.getUid())
                    .set(usuarioAtual)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao atualizar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
} 