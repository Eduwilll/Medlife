package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.model.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressFormFragment extends Fragment {
    private EditText editCep, editLogradouro, editNumero, editComplemento, editBairro, editCidade, editEstado;
    private Button buttonAddAddress;
    private Button buttonSaveChanges;
    private FirebaseFirestore db;
    private Usuario usuarioAtual;
    private OnAddressFormListener listener;
    private boolean isEditMode = false;
    private int editingPosition = -1;

    public interface OnAddressFormListener {
        void onAddressAdded();
        void onAddressUpdated();
        void onFormCancelled();
    }

    public void setOnAddressFormListener(OnAddressFormListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_address_form, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editCep = view.findViewById(R.id.editCep);
        editLogradouro = view.findViewById(R.id.editLogradouro);
        editNumero = view.findViewById(R.id.editNumero);
        editComplemento = view.findViewById(R.id.editComplemento);
        editBairro = view.findViewById(R.id.editBairro);
        editCidade = view.findViewById(R.id.editCidade);
        editEstado = view.findViewById(R.id.editEstado);
        buttonAddAddress = view.findViewById(R.id.buttonAddAddress);
        buttonSaveChanges = view.findViewById(R.id.buttonSaveChanges);

        // Set click listeners
        buttonAddAddress.setOnClickListener(v -> addNewAddress());
        buttonSaveChanges.setOnClickListener(v -> saveEditedAddress());

        // Load current user
        loadCurrentUser();

        return view;
    }

    private void loadCurrentUser() {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId != null) {
            db.collection("usuarios")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            usuarioAtual = documentSnapshot.toObject(Usuario.class);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void setEditMode(boolean editMode, Map<String, Object> address, int position) {
        this.isEditMode = editMode;
        this.editingPosition = position;
        
        if (editMode && address != null) {
            // Pre-fill the form with address data
            editCep.setText((String) address.get("cep"));
            editLogradouro.setText((String) address.get("logradouro"));
            editNumero.setText((String) address.get("numero"));
            editComplemento.setText((String) address.get("complemento"));
            editBairro.setText((String) address.get("bairro"));
            editCidade.setText((String) address.get("cidade"));
            editEstado.setText((String) address.get("estado"));

            // Change button visibility
            buttonAddAddress.setVisibility(View.GONE);
            buttonSaveChanges.setVisibility(View.VISIBLE);
        } else {
            // Add mode
            clearAddressFields();
            buttonAddAddress.setVisibility(View.VISIBLE);
            buttonSaveChanges.setVisibility(View.GONE);
        }
    }

    private void addNewAddress() {
        if (usuarioAtual == null) {
            Toast.makeText(getContext(), "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate required fields
        if (editCep.getText().toString().isEmpty() || editLogradouro.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "CEP e Logradouro são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> novoEndereco = new HashMap<>();
        novoEndereco.put("cep", editCep.getText().toString());
        novoEndereco.put("logradouro", editLogradouro.getText().toString());
        novoEndereco.put("numero", editNumero.getText().toString());
        novoEndereco.put("complemento", editComplemento.getText().toString());
        novoEndereco.put("bairro", editBairro.getText().toString());
        novoEndereco.put("cidade", editCidade.getText().toString());
        novoEndereco.put("estado", editEstado.getText().toString());

        // Check if user document exists and has endereco field
        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User document exists, add address to array
                        addAddressToExistingUser(novoEndereco);
                    } else {
                        // User document doesn't exist, create it with the address
                        createUserWithAddress(novoEndereco);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao verificar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addAddressToExistingUser(Map<String, Object> novoEndereco) {
        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .update("endereco", com.google.firebase.firestore.FieldValue.arrayUnion(novoEndereco))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Endereço adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                    clearAddressFields();
                    if (listener != null) {
                        listener.onAddressAdded();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao adicionar endereço: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createUserWithAddress(Map<String, Object> novoEndereco) {
        List<Map<String, Object>> enderecos = new ArrayList<>();
        enderecos.add(novoEndereco);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", usuarioAtual.getUid());
        userData.put("nome", usuarioAtual.getNome());
        userData.put("email", usuarioAtual.getEmail());
        userData.put("endereco", enderecos);
        userData.put("enderecoPrincipal", 0); // Set first address as principal
        
        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Usuário criado com endereço!", Toast.LENGTH_SHORT).show();
                    clearAddressFields();
                    if (listener != null) {
                        listener.onAddressAdded();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao criar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveEditedAddress() {
        if (usuarioAtual == null || usuarioAtual.getEndereco() == null || 
            editingPosition < 0 || editingPosition >= usuarioAtual.getEndereco().size()) {
            Toast.makeText(getContext(), "Erro: Dados inválidos para edição.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate required fields
        if (editCep.getText().toString().isEmpty() || editLogradouro.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "CEP e Logradouro são obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the updated address
        Map<String, Object> editedAddress = new HashMap<>();
        editedAddress.put("cep", editCep.getText().toString());
        editedAddress.put("logradouro", editLogradouro.getText().toString());
        editedAddress.put("numero", editNumero.getText().toString());
        editedAddress.put("complemento", editComplemento.getText().toString());
        editedAddress.put("bairro", editBairro.getText().toString());
        editedAddress.put("cidade", editCidade.getText().toString());
        editedAddress.put("estado", editEstado.getText().toString());

        // Get current addresses and update the specific position
        List<Map<String, Object>> currentAddresses = new ArrayList<>(usuarioAtual.getEndereco());
        currentAddresses.set(editingPosition, editedAddress);

        // Update the entire endereco array
        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .update("endereco", currentAddresses)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Endereço atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    clearAddressFields();
                    if (listener != null) {
                        listener.onAddressUpdated();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao atualizar endereço: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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

    public void resetForm() {
        isEditMode = false;
        editingPosition = -1;
        clearAddressFields();
        buttonAddAddress.setVisibility(View.VISIBLE);
        buttonSaveChanges.setVisibility(View.GONE);
    }
} 