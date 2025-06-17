package medlife.com.br.fragments.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.model.Usuario;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileUserDataFragment extends Fragment {
    private EditText editNome, editEmail, editTelefone, editDataNascimento, editCpf, editOutroGenero;
    private AutoCompleteTextView editGenero;
    private Button buttonSalvar;
    private ImageButton buttonEditData;
    private FirebaseFirestore db;
    private Usuario usuarioAtual;
    private Calendar calendar;
    private TextInputLayout layoutOutroGenero;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_user_data, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editNome = view.findViewById(R.id.editNome);
        editEmail = view.findViewById(R.id.editEmail);
        editGenero = view.findViewById(R.id.editGenero);
        editTelefone = view.findViewById(R.id.editTelefone);
        editDataNascimento = view.findViewById(R.id.editDataNascimento);
        editCpf = view.findViewById(R.id.editCpf);
        buttonSalvar = view.findViewById(R.id.buttonSalvar);
        buttonEditData = view.findViewById(R.id.buttonEditData);
        layoutOutroGenero = view.findViewById(R.id.layoutOutroGenero);
        editOutroGenero = view.findViewById(R.id.editOutroGenero);

        // Initialize Calendar
        calendar = Calendar.getInstance();

        // Set date of birth click listener
        editDataNascimento.setOnClickListener(v -> showDatePickerDialog());

        // Set edit button click listener
        buttonEditData.setOnClickListener(v -> {
            setFieldsEnabled(true);
            buttonSalvar.setVisibility(View.VISIBLE);
            buttonEditData.setVisibility(View.GONE);
        });

        // Setup gender dropdown
        String[] genders = {"Masculino", "Feminino", "Outro"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, genders);
        editGenero.setAdapter(genderAdapter);

        editGenero.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedGender = (String) parent.getItemAtPosition(position);
            if (selectedGender.equals("Outro")) {
                layoutOutroGenero.setVisibility(View.VISIBLE);
            } else {
                layoutOutroGenero.setVisibility(View.GONE);
                editOutroGenero.setText(""); // Clear other gender field
            }
        });

        // Add TextWatchers for formatting
        editTelefone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Load user data
        loadUserData();

        // Set save button click listener
        buttonSalvar.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void setFieldsEnabled(boolean enabled) {
        editNome.setEnabled(enabled);
        // editEmail remains disabled always
        editTelefone.setEnabled(enabled);
        editDataNascimento.setEnabled(enabled);
        editCpf.setEnabled(enabled);
        editGenero.setEnabled(enabled);
        editOutroGenero.setEnabled(enabled);
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateOfBirthField();
        };

        new DatePickerDialog(getContext(), dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateOfBirthField() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
        editDataNascimento.setText(sdf.format(calendar.getTime()));
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
                            editTelefone.setText(usuarioAtual.getPhoneNumber());
                            editDataNascimento.setText(usuarioAtual.getDateOfBirth());
                            editCpf.setText(usuarioAtual.getCpf());

                            if ("Other".equals(usuarioAtual.getGenero()) && usuarioAtual.getOtherGender() != null) {
                                layoutOutroGenero.setVisibility(View.VISIBLE);
                                editOutroGenero.setText(usuarioAtual.getOtherGender());
                            } else {
                                layoutOutroGenero.setVisibility(View.GONE);
                                editOutroGenero.setText("");
                            }

                            setFieldsEnabled(false);
                            buttonSalvar.setVisibility(View.GONE);
                            buttonEditData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Handle case where document does not exist
                        setFieldsEnabled(false);
                        buttonSalvar.setVisibility(View.GONE);
                        buttonEditData.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setFieldsEnabled(false);
                    buttonSalvar.setVisibility(View.GONE);
                    buttonEditData.setVisibility(View.VISIBLE);
                });
    }

    private void saveUserData() {
        if (usuarioAtual != null) {
            String nome = editNome.getText().toString();
            String email = editEmail.getText().toString();
            String genero = editGenero.getText().toString();
            String telefone = editTelefone.getText().toString();
            String dataNascimento = editDataNascimento.getText().toString();
            String cpf = editCpf.getText().toString();
            String outroGenero = editOutroGenero.getText().toString();

            // Basic validation for required fields
            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || dataNascimento.isEmpty() || cpf.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show();
                return;
            }

            // CPF validation (Brazilian CPF format XXX.XXX.XXX-XX)
            if (!isValidCpf(cpf)) {
                Toast.makeText(getContext(), "CPF inválido. O CPF deve conter 11 dígitos numéricos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Phone number validation (Brazilian phone format (XX) XXXXX-XXXX)
            if (!isValidPhoneNumber(telefone)) {
                Toast.makeText(getContext(), "Telefone inválido. Use o formato (XX) XXXXX-XXXX ou (XX) XXXX-XXXX", Toast.LENGTH_SHORT).show();
                return;
            }

            usuarioAtual.setNome(nome);
            usuarioAtual.setEmail(email);
            usuarioAtual.setGenero(genero);
            usuarioAtual.setPhoneNumber(telefone);
            usuarioAtual.setDateOfBirth(dataNascimento);
            usuarioAtual.setCpf(cpf);
            if ("Other".equals(genero)) {
                usuarioAtual.setOtherGender(outroGenero);
            } else {
                usuarioAtual.setOtherGender(null);
            }

            db.collection("usuarios")
                    .document(usuarioAtual.getUid())
                    .set(usuarioAtual)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                        setFieldsEnabled(false);
                        buttonSalvar.setVisibility(View.GONE);
                        buttonEditData.setVisibility(View.VISIBLE);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao atualizar dados: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show();
        }
    }

    // CPF Validation
    private boolean isValidCpf(String cpf) {
        // CPF must have 11 digits and contain only numbers
        return cpf.matches("\\d{11}");
    }

    // Phone Number Validation (supports (XX) XXXXX-XXXX and (XX) XXXX-XXXX)
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Regex for (XX) XXXXX-XXXX or (XX) XXXX-XXXX
        Pattern pattern = Pattern.compile("^\\(\\d{2}\\)\\s?\\d{4,5}-\\d{4}$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private class PhoneNumberFormattingTextWatcher implements TextWatcher {
        private boolean isUpdating;
        private String oldText = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            oldText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isUpdating) {
                isUpdating = false;
                return;
            }

            String text = s.toString().replaceAll("[^0-9]", "");
            StringBuilder formattedText = new StringBuilder();

            if (text.length() > 0) {
                formattedText.append("(").append(text.substring(0, Math.min(2, text.length())));
                if (text.length() > 2) {
                    formattedText.append(") ");
                    if (text.length() <= 7) {
                        formattedText.append(text.substring(2));
                    } else if (text.length() == 8 || text.length() == 9) { // 8 or 9 digits after DDD
                        formattedText.append(text.substring(2, Math.min(7, text.length()))).append("-").append(text.substring(7));
                    } else {
                        formattedText.append(text.substring(2, 7)).append("-").append(text.substring(7, Math.min(11, text.length())));
                    }
                }
            }

            isUpdating = true;
            s.replace(0, s.length(), formattedText.toString());
        }
    }
} 