package medlife.com.br.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.launch
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

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autenticacao)

        inicializaComponentes()

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

    private fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@AutenticacaoActivity, request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    authViewModel.signInWithGoogle(idToken)
                } else {
                    Toast.makeText(this@AutenticacaoActivity, "Tipo de credencial não suportado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: GetCredentialException) {
                Toast.makeText(this@AutenticacaoActivity, "Erro ao obter credencial: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@AutenticacaoActivity, "Erro inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
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
