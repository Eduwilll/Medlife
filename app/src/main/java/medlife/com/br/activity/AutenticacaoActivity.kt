package medlife.com.br.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import medlife.com.br.R
import medlife.com.br.viewmodel.AuthViewModel

class AutenticacaoActivity : AppCompatActivity() {

    private lateinit var campoEmail: TextInputEditText
    private lateinit var campoSenha: TextInputEditText
    private lateinit var buttonAcessar: MaterialButton
    private lateinit var registerLink: TextView
    private lateinit var forgotPasswordLink: TextView
    private lateinit var googleButton: MaterialButton
    private lateinit var facebookButton: MaterialButton

    private lateinit var googleSignInClient: GoogleSignInClient
    private val authViewModel: AuthViewModel by viewModels()

    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao)

        inicializaComponentes()
        configurarGoogleSignIn()

        verificarUsuarioLogado()
        setupObservers()

        buttonAcessar.setOnClickListener {
            val email = campoEmail.text.toString()
            val senha = campoSenha.text.toString()

            if (email.isNotEmpty()) {
                if (senha.isNotEmpty()) {
                    authViewModel.signIn(email, senha)
                } else {
                    Toast.makeText(this, "Preencha a senha!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha o E-mail!", Toast.LENGTH_SHORT).show()
            }
        }

        registerLink.setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }

        forgotPasswordLink.setOnClickListener {
            Toast.makeText(this, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
        }

        googleButton.setOnClickListener { signInWithGoogle() }

        facebookButton.setOnClickListener {
            Toast.makeText(this, "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        authViewModel.signInResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Logado com sucesso", Toast.LENGTH_SHORT).show()
                abrirTelaPrincipal()
            }.onFailure { exception ->
                val erroExcecao = when (exception) {
                    is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválidos"
                    else -> "Erro ao fazer login: ${exception.message}"
                }
                Toast.makeText(this, "Erro: $erroExcecao", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account?.idToken
                if (idToken != null) {
                    authViewModel.signInWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Erro ao fazer login com Google: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificarUsuarioLogado() {
        if (authViewModel.getCurrentUser() != null) {
            abrirTelaPrincipal()
        }
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish()
    }

    private fun inicializaComponentes() {
        campoEmail = findViewById(R.id.editCadastroEmail)
        campoSenha = findViewById(R.id.editCadastroSenha)
        buttonAcessar = findViewById(R.id.buttonAcesso)
        registerLink = findViewById(R.id.registerLink)
        forgotPasswordLink = findViewById(R.id.forgotPasswordLink)
        googleButton = findViewById(R.id.googleButton)
        facebookButton = findViewById(R.id.facebookButton)
    }
}
