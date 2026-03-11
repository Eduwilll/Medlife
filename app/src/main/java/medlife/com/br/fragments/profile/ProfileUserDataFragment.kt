package medlife.com.br.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.activity.AutenticacaoActivity
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.model.Usuario

class ProfileUserDataFragment : Fragment() {
    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var editCpf: EditText
    private lateinit var buttonSaveData: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var usuarioAtual: Usuario? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_user_data, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editName = view.findViewById(R.id.editNome)
        editEmail = view.findViewById(R.id.editEmail)
        editPhone = view.findViewById(R.id.editTelefone)
        editCpf = view.findViewById(R.id.editCpf)
        buttonSaveData = view.findViewById(R.id.buttonSalvar)

        loadUserData()

        buttonSaveData.setOnClickListener { saveUserData() }

        return view
    }

    private fun loadUserData() {
        val userId = UsuarioFirebase.idUsuario
        if (userId != null) {
            db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        usuarioAtual = documentSnapshot.toObject(Usuario::class.java)
                        if (usuarioAtual != null) {
                            editName.setText(usuarioAtual!!.nome)
                            editEmail.setText(usuarioAtual!!.email)
                            editPhone.setText(usuarioAtual!!.phoneNumber)
                            editCpf.setText(usuarioAtual!!.cpf)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao carregar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserData() {
        if (usuarioAtual != null) {
            val nome = editName.text.toString()
            val telefone = editPhone.text.toString()
            val cpf = editCpf.text.toString()

            if (nome.isEmpty()) {
                Toast.makeText(context, "O nome não pode ser vazio", Toast.LENGTH_SHORT).show()
                return
            }

            usuarioAtual!!.nome = nome
            usuarioAtual!!.phoneNumber = telefone
            usuarioAtual!!.cpf = cpf

            db.collection("usuarios")
                .document(usuarioAtual!!.uid!!)
                .set(usuarioAtual!!)
                .addOnSuccessListener {
                    Toast.makeText(context, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao atualizar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
