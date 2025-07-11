package medlife.com.br.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import medlife.com.br.R;
import medlife.com.br.helper.ConfiguracaoFirebase;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.model.Usuario;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private CheckBox checkboxConsent;
    private MaterialButton buttonRegister;
    private TextView textLoginLink;
    private FirebaseAuth autenticacao;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        db = FirebaseFirestore.getInstance();

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

        setupLoginLink();
    }

    private void setupLoginLink() {
        String fullText = getString(R.string.j_possui_conta);
        SpannableString spannableString = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                finish(); // Close RegisterActivity and go back to AutenticacaoActivity (Login)
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(ContextCompat.getColor(RegisterActivity.this, R.color.link_blue));
            }
        };

        // Find the start and end index of the word "Entrar" in the string resource.
        // Assuming "j_possui_conta" is "Já possui conta? Entrar"
        int startIndex = fullText.indexOf("Entrar");
        if (startIndex != -1) {
            int endIndex = startIndex + "Entrar".length();
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textLoginLink.setText(spannableString);
        textLoginLink.setMovementMethod(LinkMovementMethod.getInstance());
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
                                            // Create user document in Firestore
                                            Usuario usuario = new Usuario();
                                            usuario.setUid(user.getUid());
                                            usuario.setNome(nome);
                                            usuario.setEmail(email);
                                            usuario.setPhoneNumber("");
                                            usuario.setEndereco(new ArrayList<>());
                                            usuario.setLastLogin(com.google.firebase.Timestamp.now());

                                            db.collection("usuarios")
                                                    .document(user.getUid())
                                                    .set(usuario)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(RegisterActivity.this,
                                                                        "Cadastro realizado com sucesso!",
                                                                        Toast.LENGTH_SHORT).show();
                                                                abrirTelaPrincipal("U");
                                                            } else {
                                                                Toast.makeText(RegisterActivity.this,
                                                                        "Erro ao salvar dados do usuário: " + task.getException().getMessage(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Erro ao atualizar perfil: " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
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
        
        // Make Terms of Use text clickable
        TextView textConsent = findViewById(R.id.textConsent);
        setupClickableTermsOfUse(textConsent);
    }

    private void setupClickableTermsOfUse(TextView textConsent) {
        String fullText = getString(R.string.politica_de_privacidade);
        SpannableString spannableString = new SpannableString(fullText);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Toggle checkbox when text is clicked
                checkboxConsent.setChecked(!checkboxConsent.isChecked());
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(RegisterActivity.this, R.color.link_blue));
            }
        };

        // Make the entire text clickable
        spannableString.setSpan(clickableSpan, 0, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textConsent.setText(spannableString);
        textConsent.setMovementMethod(LinkMovementMethod.getInstance());
    }
}