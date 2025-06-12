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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.model.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileAddressesFragment extends Fragment {
    private RecyclerView recyclerAddresses;
    private Button buttonAddAddress;
    private EditText editCep, editLogradouro, editNumero, editComplemento, editBairro, editCidade, editEstado;
    private FirebaseFirestore db;
    private Usuario usuarioAtual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_addresses, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerAddresses = view.findViewById(R.id.recyclerAddresses);
        buttonAddAddress = view.findViewById(R.id.buttonAddAddress);
        editCep = view.findViewById(R.id.editCep);
        editLogradouro = view.findViewById(R.id.editLogradouro);
        editNumero = view.findViewById(R.id.editNumero);
        editComplemento = view.findViewById(R.id.editComplemento);
        editBairro = view.findViewById(R.id.editBairro);
        editCidade = view.findViewById(R.id.editCidade);
        editEstado = view.findViewById(R.id.editEstado);

        // Setup RecyclerView
        recyclerAddresses.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load user addresses
        loadUserAddresses();

        // Set add address button click listener
        buttonAddAddress.setOnClickListener(v -> addNewAddress());

        return view;
    }

    private void loadUserAddresses() {
        String userId = UsuarioFirebase.getIdUsuario();
        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usuarioAtual = documentSnapshot.toObject(Usuario.class);
                        if (usuarioAtual != null && usuarioAtual.getEndereco() != null) {
                            // TODO: Implement address adapter and display addresses
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao carregar endereços: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addNewAddress() {
        if (usuarioAtual != null) {
            Map<String, Object> novoEndereco = new HashMap<>();
            novoEndereco.put("cep", editCep.getText().toString());
            novoEndereco.put("logradouro", editLogradouro.getText().toString());
            novoEndereco.put("numero", editNumero.getText().toString());
            novoEndereco.put("complemento", editComplemento.getText().toString());
            novoEndereco.put("bairro", editBairro.getText().toString());
            novoEndereco.put("cidade", editCidade.getText().toString());
            novoEndereco.put("estado", editEstado.getText().toString());

            if (usuarioAtual.getEndereco() == null) {
                usuarioAtual.setEndereco(new ArrayList<>());
            }
            usuarioAtual.getEndereco().add(novoEndereco);

            db.collection("usuarios")
                    .document(usuarioAtual.getUid())
                    .set(usuarioAtual)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Endereço adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                        clearAddressFields();
                        loadUserAddresses();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao adicionar endereço: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void clearAddressFields() {
        editCep.setText("");
        editLogradouro.setText("");
        editNumero.setText("");
        editComplemento.setText("");
        editBairro.setText("");
        editCidade.setText("");
        editEstado.setText("");
    }
} 