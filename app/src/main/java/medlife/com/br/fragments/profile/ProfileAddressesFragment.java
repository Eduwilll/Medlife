package medlife.com.br.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;

public class ProfileAddressesFragment extends Fragment {
    private RecyclerView recyclerAddresses;
    private Button buttonAddAddress;
    private Button buttonToggleAddAddress;
    private ScrollView scrollViewAddAddress;
    private TextView textEmpty;
    private EditText editCep, editLogradouro, editNumero, editComplemento, editBairro, editCidade, editEstado;
    private Button buttonSaveChanges;
    private FirebaseFirestore db;
    private Usuario usuarioAtual;
    private AddressAdapter addressAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_addresses, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerAddresses = view.findViewById(R.id.recyclerAddresses);
        buttonAddAddress = view.findViewById(R.id.buttonAddAddress);
        buttonToggleAddAddress = view.findViewById(R.id.buttonToggleAddAddress);
        scrollViewAddAddress = view.findViewById(R.id.scrollViewAddAddress);
        textEmpty = view.findViewById(R.id.textEmpty);
        editCep = view.findViewById(R.id.editCep);
        editLogradouro = view.findViewById(R.id.editLogradouro);
        editNumero = view.findViewById(R.id.editNumero);
        editComplemento = view.findViewById(R.id.editComplemento);
        editBairro = view.findViewById(R.id.editBairro);
        editCidade = view.findViewById(R.id.editCidade);
        editEstado = view.findViewById(R.id.editEstado);
        buttonSaveChanges = view.findViewById(R.id.buttonSaveChanges);

        // Setup RecyclerView
        recyclerAddresses.setLayoutManager(new LinearLayoutManager(getContext()));
        addressAdapter = new AddressAdapter(new ArrayList<>(), usuarioAtual); // Initialize with empty list and current user
        recyclerAddresses.setAdapter(addressAdapter);

        // Load user addresses
        loadUserAddresses();

        // Set add address button click listener
        buttonAddAddress.setOnClickListener(v -> addNewAddress());
        buttonSaveChanges.setOnClickListener(v -> saveEditedAddress());

        // Set toggle add address form button click listener
        buttonToggleAddAddress.setOnClickListener(v -> {
            if (scrollViewAddAddress.getVisibility() == View.GONE) {
                scrollViewAddAddress.setVisibility(View.VISIBLE);
                buttonToggleAddAddress.setText(R.string.ocultar_formul_rio);
                buttonAddAddress.setVisibility(View.VISIBLE);
                buttonSaveChanges.setVisibility(View.GONE);
            } else {
                scrollViewAddAddress.setVisibility(View.GONE);
                buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o);
                buttonAddAddress.setVisibility(View.VISIBLE);
                buttonSaveChanges.setVisibility(View.GONE);
                clearAddressFields();
            }
        });

        return view;
    }

    private void loadUserAddresses() {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId == null) {
            Toast.makeText(getContext(), "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        System.out.println("Loading addresses for user: " + userId);
        
        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usuarioAtual = documentSnapshot.toObject(Usuario.class);
                        if (usuarioAtual != null && usuarioAtual.getEndereco() != null && !usuarioAtual.getEndereco().isEmpty()) {
                            System.out.println("Found " + usuarioAtual.getEndereco().size() + " addresses for user");
                            addressAdapter.updateAddresses(usuarioAtual.getEndereco(), usuarioAtual);
                            recyclerAddresses.setVisibility(View.VISIBLE);
                            textEmpty.setVisibility(View.GONE);
                        } else {
                            System.out.println("No addresses found for user");
                            recyclerAddresses.setVisibility(View.GONE);
                            textEmpty.setVisibility(View.VISIBLE);
                        }
                    } else {
                        System.out.println("User document doesn't exist");
                        recyclerAddresses.setVisibility(View.GONE);
                        textEmpty.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error loading addresses: " + e.getMessage());
                    Toast.makeText(getContext(), "Erro ao carregar endereços: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    recyclerAddresses.setVisibility(View.GONE);
                    textEmpty.setVisibility(View.VISIBLE);
                });
    }

    private void addNewAddress() {
        // Basic validation for required fields
        if (usuarioAtual != null) {
            Map<String, Object> novoEndereco = new HashMap<>();
            novoEndereco.put("cep", editCep.getText().toString());
            novoEndereco.put("logradouro", editLogradouro.getText().toString());
            novoEndereco.put("numero", editNumero.getText().toString());
            novoEndereco.put("complemento", editComplemento.getText().toString());
            novoEndereco.put("bairro", editBairro.getText().toString());
            novoEndereco.put("cidade", editCidade.getText().toString());
            novoEndereco.put("estado", editEstado.getText().toString());

            // Validate required fields (example: CEP and Logradouro)
            if (editCep.getText().toString().isEmpty() || editLogradouro.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "CEP e Logradouro são obrigatórios.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log the address being added
            System.out.println("Adding new address: " + novoEndereco.toString());

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
                        System.out.println("Error checking user document: " + e.getMessage());
                        Toast.makeText(getContext(), "Erro ao verificar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void addAddressToExistingUser(Map<String, Object> novoEndereco) {
        // Use arrayUnion to add the new address to the endereco array
        db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .update("endereco", com.google.firebase.firestore.FieldValue.arrayUnion(novoEndereco))
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Address added successfully to Firestore");
                    Toast.makeText(getContext(), "Endereço adicionado com sucesso!", Toast.LENGTH_SHORT).show();
                    clearAddressFields();
                    loadUserAddresses(); // Reload addresses to update UI
                    scrollViewAddAddress.setVisibility(View.GONE); // Hide form after adding
                    buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o);
                    buttonAddAddress.setVisibility(View.VISIBLE);
                    buttonSaveChanges.setVisibility(View.GONE);
                    
                    // Show Snackbar with navigation option
                    showAddressAddedSnackbar();
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error adding address: " + e.getMessage());
                    Toast.makeText(getContext(), "Erro ao adicionar endereço: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    private void createUserWithAddress(Map<String, Object> novoEndereco) {
        // Create new user document with the address
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
                    System.out.println("User created with address successfully");
                    Toast.makeText(getContext(), "Usuário criado com endereço!", Toast.LENGTH_SHORT).show();
                    clearAddressFields();
                    loadUserAddresses(); // Reload addresses to update UI
                    scrollViewAddAddress.setVisibility(View.GONE); // Hide form after adding
                    buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o);
                    buttonAddAddress.setVisibility(View.VISIBLE);
                    buttonSaveChanges.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error creating user with address: " + e.getMessage());
                    Toast.makeText(getContext(), "Erro ao criar usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveEditedAddress() {
        int editingPosition = (Integer) buttonSaveChanges.getTag(); // Retrieve the position of the address being edited
        if (usuarioAtual != null && usuarioAtual.getEndereco() != null && editingPosition >= 0 && editingPosition < usuarioAtual.getEndereco().size()) {
            
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
                        loadUserAddresses();
                        scrollViewAddAddress.setVisibility(View.GONE);
                        buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o);
                        buttonAddAddress.setVisibility(View.VISIBLE);
                        buttonSaveChanges.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Erro ao atualizar endereço: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(), "Erro ao salvar alterações. Endereço não encontrado ou usuário inválido.", Toast.LENGTH_SHORT).show();
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

    // AddressAdapter class (to be implemented in more detail)
    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
        private List<Map<String, Object>> addresses;
        private Usuario usuarioAtual;

        public AddressAdapter(List<Map<String, Object>> addresses, Usuario usuarioAtual) {
            this.addresses = addresses;
            this.usuarioAtual = usuarioAtual;
        }

        public void updateAddresses(List<Map<String, Object>> newAddresses, Usuario usuarioAtual) {
            this.addresses.clear();
            this.addresses.addAll(newAddresses);
            this.usuarioAtual = usuarioAtual;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
            return new AddressViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
            Map<String, Object> address = addresses.get(position);
            holder.bind(address, position, usuarioAtual);
        }

        @Override
        public int getItemCount() {
            return addresses.size();
        }

        class AddressViewHolder extends RecyclerView.ViewHolder {
            private TextView textAddressLine1;
            private TextView textAddressLine2;
            private ImageButton buttonEditAddress;
            private ImageButton buttonDeleteAddress;
            private Button buttonSetPrincipal;
            private TextView textPrincipal;

            public AddressViewHolder(@NonNull View itemView) {
                super(itemView);
                textAddressLine1 = itemView.findViewById(R.id.textAddressLine1);
                textAddressLine2 = itemView.findViewById(R.id.textAddressLine2);
                buttonEditAddress = itemView.findViewById(R.id.buttonEditAddress);
                buttonDeleteAddress = itemView.findViewById(R.id.buttonDeleteAddress);
                buttonSetPrincipal = itemView.findViewById(R.id.buttonSetPrincipal);
                textPrincipal = itemView.findViewById(R.id.textPrincipal);
            }

            public void bind(Map<String, Object> address, int position, Usuario usuarioAtual) {
                String logradouro = (String) address.get("logradouro");
                String numero = (String) address.get("numero");
                String complemento = (String) address.get("complemento");
                String bairro = (String) address.get("bairro");
                String cidade = (String) address.get("cidade");
                String estado = (String) address.get("estado");
                String cep = (String) address.get("cep");

                String addressLine1 = String.format("%s, %s%s", logradouro, numero, (complemento != null && !complemento.isEmpty() ? " - " + complemento : ""));
                String addressLine2 = String.format("%s, %s - %s, %s", bairro, cidade, estado, cep);

                textAddressLine1.setText(addressLine1);
                textAddressLine2.setText(addressLine2);

                boolean isPrincipal = (usuarioAtual != null && usuarioAtual.getEnderecoPrincipal() == position);
                textPrincipal.setVisibility(isPrincipal ? View.VISIBLE : View.GONE);
                buttonSetPrincipal.setVisibility(isPrincipal ? View.GONE : View.VISIBLE);

                // Optionally, highlight the background for the principal address
                itemView.setBackgroundResource(isPrincipal ? R.drawable.bg_address_principal_card : android.R.color.transparent);

                buttonSetPrincipal.setOnClickListener(v -> setPrincipalAddress(position));

                buttonEditAddress.setOnClickListener(v -> {
                    editAddress(address, getAdapterPosition());
                });

                buttonDeleteAddress.setOnClickListener(v -> {
                    deleteAddress(address, getAdapterPosition());
                });
            }
        }
    }

    private void editAddress(Map<String, Object> address, int position) {
        // Show the form
        scrollViewAddAddress.setVisibility(View.VISIBLE);
        buttonToggleAddAddress.setText(R.string.ocultar_formul_rio);

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
        buttonSaveChanges.setTag(position); // Store the position of the address being edited
    }

    private void deleteAddress(Map<String, Object> address, int position) {
        if (usuarioAtual != null && usuarioAtual.getEndereco() != null) {
            // Get current addresses and remove the specific position
            List<Map<String, Object>> currentAddresses = new ArrayList<>(usuarioAtual.getEndereco());
            currentAddresses.remove(position);
            
            // Update the enderecoPrincipal index if needed
            int currentPrincipal = usuarioAtual.getEnderecoPrincipal();
            if (currentPrincipal == position) {
                // If we're deleting the principal address, set it to 0 (first address) or -1 if no addresses left
                usuarioAtual.setEnderecoPrincipal(currentAddresses.isEmpty() ? -1 : 0);
            } else if (currentPrincipal > position) {
                // If principal address is after the deleted one, adjust the index
                usuarioAtual.setEnderecoPrincipal(currentPrincipal - 1);
            }

            // Update both endereco array and enderecoPrincipal
            db.collection("usuarios")
                    .document(usuarioAtual.getUid())
                    .update("endereco", currentAddresses, "enderecoPrincipal", usuarioAtual.getEnderecoPrincipal())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Endereço removido com sucesso!", Toast.LENGTH_SHORT).show();
                        loadUserAddresses(); // Reload addresses to update UI
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao remover endereço: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Erro: Usuário ou endereços inválidos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPrincipalAddress(int position) {
        if (usuarioAtual != null) {
            db.collection("usuarios")
                .document(usuarioAtual.getUid())
                .update("enderecoPrincipal", position)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Endereço principal atualizado!", Toast.LENGTH_SHORT).show();
                    loadUserAddresses();
                    addressAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao atualizar endereço principal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void showAddressAddedSnackbar() {
        if (getView() != null) {
            com.google.android.material.snackbar.Snackbar.make(
                getView(),
                "Endereço adicionado! Voltar ao carrinho?",
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
            ).setAction("Voltar", v -> {
                // Navigate back to cart
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }).show();
        }
    }
} 