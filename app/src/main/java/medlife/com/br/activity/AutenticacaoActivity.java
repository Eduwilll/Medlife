package medlife.com.br.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;

import medlife.com.br.R;
import medlife.com.br.helper.ConfiguracaoFirebase;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.model.Usuario;

public class AutenticacaoActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private MaterialButton buttonAcessar;
    private TextView registerLink, forgotPasswordLink;
    private MaterialButton googleButton, facebookButton;
    private FirebaseAuth autenticacao;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);

        inicializaComponentes();
        configurarGoogleSignIn();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        db = FirebaseFirestore.getInstance();
//        autenticacao.signOut();

        //Verificar usuario logado
        verificarUsuarioLogado();

        buttonAcessar.setOnClickListener(v -> {
            String email = campoEmail.getText().toString();
            String senha = campoSenha.getText().toString();

            if (!email.isEmpty()) {
                if (!senha.isEmpty()) {
                    autenticarUsuario(email, senha);
                } else {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AutenticacaoActivity.this,
                        "Preencha o E-mail!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        registerLink.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        forgotPasswordLink.setOnClickListener(v -> Toast.makeText(AutenticacaoActivity.this,
                "Funcionalidade em desenvolvimento",
                Toast.LENGTH_SHORT).show());

        googleButton.setOnClickListener(v -> signInWithGoogle());

        facebookButton.setOnClickListener(v -> Toast.makeText(AutenticacaoActivity.this,
                "Funcionalidade em desenvolvimento",
                Toast.LENGTH_SHORT).show());
    }

    private void configurarGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Erro ao fazer login com Google: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = autenticacao.getCurrentUser();
                        if (user != null) {
                            // Save user data to Firestore
                            salvarUsuarioFirestore(user);
                        } else {
                            abrirTelaPrincipal();
                        }
                    } else {
                        Toast.makeText(AutenticacaoActivity.this,
                                "Erro ao autenticar com Google: " + Objects.requireNonNull(task.getException()).getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void autenticarUsuario(String email, String senha) {
        autenticacao.signInWithEmailAndPassword(
                email, senha
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = autenticacao.getCurrentUser();
                if (user != null) {
                    // Check if user exists in Firestore, if not, save them
                    verificarESalvarUsuarioFirestore(user);
                } else {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Logado com sucesso",
                            Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                }
            } else {
                String erroExcecao;
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    erroExcecao = "E-mail ou senha inválidos";
                } catch (Exception e) {
                    erroExcecao = "Erro ao fazer login: " + e.getMessage();
                }
                Toast.makeText(AutenticacaoActivity.this,
                        "Erro: " + erroExcecao,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarESalvarUsuarioFirestore(FirebaseUser firebaseUser) {
        db.collection("usuarios").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // User already exists in Firestore, update lastLogin
                        atualizarLastLogin(firebaseUser.getUid());
                    } else {
                        // User doesn't exist, save them
                        salvarUsuarioFirestore(firebaseUser);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error checking user, try to save anyway
                    salvarUsuarioFirestore(firebaseUser);
                });
    }

    private void salvarUsuarioFirestore(FirebaseUser firebaseUser) {
        Usuario usuario = new Usuario();
        usuario.setUid(firebaseUser.getUid());
        usuario.setEmail(firebaseUser.getEmail());
        
        // Set name from display name or email
        String nome = firebaseUser.getDisplayName();
        if (nome == null || nome.isEmpty()) {
            nome = firebaseUser.getEmail();
        }
        usuario.setNome(nome);
        
        // Set current timestamp as lastLogin
        usuario.setLastLogin(com.google.firebase.Timestamp.now());

        db.collection("usuarios").document(firebaseUser.getUid())
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Usuário salvo com sucesso",
                            Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Erro ao salvar usuário: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    // Still open main screen even if save fails
                    abrirTelaPrincipal();
                });
    }
    
    private void atualizarLastLogin(String userId) {
        db.collection("usuarios").document(userId)
                .update("lastLogin", com.google.firebase.Timestamp.now())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AutenticacaoActivity.this,
                            "Logado com sucesso",
                            Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                })
                .addOnFailureListener(e -> {
                    // Even if update fails, still allow login
                    Toast.makeText(AutenticacaoActivity.this,
                            "Logado com sucesso",
                            Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                });
    }

    private void verificarUsuarioLogado() {
        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null) {
            String tipoUsuario = usuarioAtual.getDisplayName();
            abrirTelaPrincipal();
        }
    }

    private void abrirTelaPrincipal() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    private void inicializaComponentes() {
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        buttonAcessar = findViewById(R.id.buttonAcesso);
        registerLink = findViewById(R.id.registerLink);
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink);
        googleButton = findViewById(R.id.googleButton);
        facebookButton = findViewById(R.id.facebookButton);
    }
}
