package medlife.com.br.activity

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import medlife.com.br.R
import medlife.com.br.model.Usuario
import medlife.com.br.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var campoNome: TextInputEditText
    private lateinit var campoEmail: TextInputEditText
    private lateinit var campoSenha: TextInputEditText
    private lateinit var checkboxConsent: CheckBox
    private lateinit var buttonRegister: MaterialButton
    private lateinit var textLoginLink: TextView

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        inicializaComponentes()
        setupObservers()

        buttonRegister.setOnClickListener {
            val nome = campoNome.text.toString()
            val email = campoEmail.text.toString()
            val senha = campoSenha.text.toString()

            if (nome.isNotEmpty()) {
                if (email.isNotEmpty()) {
                    if (senha.isNotEmpty()) {
                        if (checkboxConsent.isChecked) {
                            val usuario = Usuario().apply {
                                this.nome = nome
                                this.email = email
                                this.phoneNumber = ""
                                this.endereco = emptyList()
                            }
                            authViewModel.register(email, senha, usuario)
                        } else {
                            Toast.makeText(this, "Você deve aceitar os Termos de Serviço e Política de Privacidade", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Preencha a senha!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Preencha o E-mail!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Preencha o nome!", Toast.LENGTH_SHORT).show()
            }
        }

        setupLoginLink()
    }

    private fun setupObservers() {
        authViewModel.registerResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                abrirTelaPrincipal()
            }.onFailure { exception ->
                val erroExcecao = when (exception) {
                    is FirebaseAuthWeakPasswordException -> "Digite uma senha mais forte!"
                    is FirebaseAuthInvalidCredentialsException -> "Por favor, digite um e-mail válido"
                    is FirebaseAuthUserCollisionException -> "Esta conta já foi cadastrada"
                    else -> "ao cadastrar usuário: ${exception.message}"
                }
                Toast.makeText(this, "Erro: $erroExcecao", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLoginLink() {
        val fullText = getString(R.string.j_possui_conta)
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                finish() // Close RegisterActivity and go back to AutenticacaoActivity (Login)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.link_blue)
            }
        }

        val startIndex = fullText.indexOf("Entrar")
        if (startIndex != -1) {
            val endIndex = startIndex + "Entrar".length
            spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textLoginLink.text = spannableString
        textLoginLink.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun abrirTelaPrincipal() {
        startActivity(Intent(applicationContext, HomeActivity::class.java))
        finish() // Close RegisterActivity
    }

    private fun inicializaComponentes() {
        campoNome = findViewById(R.id.editNomeCompleto)
        campoEmail = findViewById(R.id.editCadastroEmail)
        campoSenha = findViewById(R.id.editCadastroSenha)
        checkboxConsent = findViewById(R.id.checkboxConsent)
        buttonRegister = findViewById(R.id.buttonRegister)
        textLoginLink = findViewById(R.id.textLoginLink)

        val textConsent: TextView = findViewById(R.id.textConsent)
        setupClickableTermsOfUse(textConsent)
    }

    private fun setupClickableTermsOfUse(textConsent: TextView) {
        val fullText = getString(R.string.politica_de_privacidade)
        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                checkboxConsent.isChecked = !checkboxConsent.isChecked
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = true
                ds.color = ContextCompat.getColor(this@RegisterActivity, R.color.link_blue)
            }
        }

        spannableString.setSpan(clickableSpan, 0, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textConsent.text = spannableString
        textConsent.movementMethod = LinkMovementMethod.getInstance()
    }
}
