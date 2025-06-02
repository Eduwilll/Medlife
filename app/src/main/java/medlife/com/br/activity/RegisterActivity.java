package medlife.com.br.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import medlife.com.br.R;
import medlife.com.br.helper.ConfiguracaoFirebase;
import medlife.com.br.helper.UsuarioFirebase;

public class RegisterActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private CheckBox checkboxConsent;
    private MaterialButton buttonRegister;
    private TextView textLoginLink;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //getSupportActionBar().hide(); // Hide action bar if needed

        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = campoNome.getText().toString();
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!nome.isEmpty()) {
                    if (!email.isEmpty()) {
                        if (!senha.isEmpty()) {
                            if (checkboxConsent.isChecked()) {
                                createUser(nome, email, senha);
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "Você deve aceitar os Termos de Serviço e Política de Privacidade",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Preencha a senha!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Preencha o E-mail!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Preencha o nome!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        textLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close RegisterActivity and go back to AutenticacaoActivity (Login)
            }
        });
    }

    private void createUser(final String nome, String email, String senha) {
        autenticacao.createUserWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = autenticacao.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nome)
                                .build();

                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Cadastro realizado com sucesso!",
                                                    Toast.LENGTH_SHORT).show();
                                            // Assuming a default user type 'U' for registration
                                            abrirTelaPrincipal("U"); // Navigate to home activity
                                        } else {
                                            // Handle the error in updating profile if necessary
                                            Toast.makeText(RegisterActivity.this,
                                                    "Erro ao atualizar perfil: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            // You might still want to proceed or handle this differently
                                            abrirTelaPrincipal("U");
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Erro ao obter usuário após cadastro",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erroExcecao = "Digite uma senha mais forte!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Por favor, digite um e-mail válido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Este conta já foi cadastrada";
                    } catch (Exception e) {
                        erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this,
                            "Erro: " + erroExcecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void abrirTelaPrincipal(String tipoUsuario) {
        // Assuming 'U' for standard users after registration
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish(); // Close RegisterActivity
    }

    private void inicializaComponentes() {
        campoNome = findViewById(R.id.editNomeCompleto);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        checkboxConsent = findViewById(R.id.checkboxConsent);
        buttonRegister = findViewById(R.id.buttonRegister);
        textLoginLink = findViewById(R.id.textLoginLink);
    }
}