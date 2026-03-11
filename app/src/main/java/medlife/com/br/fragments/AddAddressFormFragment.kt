package medlife.com.br.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.model.Usuario

class AddAddressFormFragment : Fragment() {
    private lateinit var editCep: EditText
    private lateinit var editLogradouro: EditText
    private lateinit var editNumero: EditText
    private lateinit var editComplemento: EditText
    private lateinit var editBairro: EditText
    private lateinit var editCidade: EditText
    private lateinit var editEstado: EditText
    private lateinit var buttonAddAddress: Button
    private lateinit var buttonSaveChanges: Button
    private lateinit var db: FirebaseFirestore
    private var usuarioAtual: Usuario? = null
    private var listener: OnAddressFormListener? = null
    private var isEditMode = false
    private var editingPosition = -1

    interface OnAddressFormListener {
        fun onAddressAdded()
        fun onAddressUpdated()
        fun onFormCancelled()
    }

    fun setOnAddressFormListener(listener: OnAddressFormListener?) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_address_form, container, false)

        db = FirebaseFirestore.getInstance()

        editCep = view.findViewById(R.id.editCep)
        editLogradouro = view.findViewById(R.id.editLogradouro)
        editNumero = view.findViewById(R.id.editNumero)
        editComplemento = view.findViewById(R.id.editComplemento)
        editBairro = view.findViewById(R.id.editBairro)
        editCidade = view.findViewById(R.id.editCidade)
        editEstado = view.findViewById(R.id.editEstado)
        buttonAddAddress = view.findViewById(R.id.buttonAddAddress)
        buttonSaveChanges = view.findViewById(R.id.buttonSaveChanges)

        buttonAddAddress.setOnClickListener { addNewAddress() }
        buttonSaveChanges.setOnClickListener { saveEditedAddress() }

        loadCurrentUser()

        return view
    }

    private fun loadCurrentUser() {
        val userId = UsuarioFirebase.idUsuario
        if (userId != null) {
            db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        usuarioAtual = documentSnapshot.toObject(Usuario::class.java)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao carregar dados do usuário", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun setEditMode(editMode: Boolean, address: Map<String, Any>?, position: Int) {
        this.isEditMode = editMode
        this.editingPosition = position

        if (editMode && address != null) {
            editCep.setText(address["cep"] as? String ?: "")
            editLogradouro.setText(address["logradouro"] as? String ?: "")
            editNumero.setText(address["numero"] as? String ?: "")
            editComplemento.setText(address["complemento"] as? String ?: "")
            editBairro.setText(address["bairro"] as? String ?: "")
            editCidade.setText(address["cidade"] as? String ?: "")
            editEstado.setText(address["estado"] as? String ?: "")

            buttonAddAddress.visibility = View.GONE
            buttonSaveChanges.visibility = View.VISIBLE
        } else {
            clearAddressFields()
            buttonAddAddress.visibility = View.VISIBLE
            buttonSaveChanges.visibility = View.GONE
        }
    }

    private fun addNewAddress() {
        if (usuarioAtual == null) {
            Toast.makeText(context, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        if (editCep.text.toString().isEmpty() || editLogradouro.text.toString().isEmpty()) {
            Toast.makeText(context, "CEP e Logradouro são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val novoEndereco = hashMapOf<String, Any>(
            "cep" to editCep.text.toString(),
            "logradouro" to editLogradouro.text.toString(),
            "numero" to editNumero.text.toString(),
            "complemento" to editComplemento.text.toString(),
            "bairro" to editBairro.text.toString(),
            "cidade" to editCidade.text.toString(),
            "estado" to editEstado.text.toString()
        )

        db.collection("usuarios")
            .document(usuarioAtual!!.uid!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    addAddressToExistingUser(novoEndereco)
                } else {
                    createUserWithAddress(novoEndereco)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao verificar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addAddressToExistingUser(novoEndereco: Map<String, Any>) {
        db.collection("usuarios")
            .document(usuarioAtual!!.uid!!)
            .update("endereco", FieldValue.arrayUnion(novoEndereco))
            .addOnSuccessListener {
                Toast.makeText(context, "Endereço adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                clearAddressFields()
                listener?.onAddressAdded()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao adicionar endereço: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createUserWithAddress(novoEndereco: Map<String, Any>) {
        val enderecos = mutableListOf<Map<String, Any>>(novoEndereco)

        val userData = hashMapOf<String, Any?>(
            "uid" to usuarioAtual!!.uid,
            "nome" to usuarioAtual!!.nome,
            "email" to usuarioAtual!!.email,
            "endereco" to enderecos,
            "enderecoPrincipal" to 0
        )

        db.collection("usuarios")
            .document(usuarioAtual!!.uid!!)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(context, "Usuário criado com endereço!", Toast.LENGTH_SHORT).show()
                clearAddressFields()
                listener?.onAddressAdded()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao criar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveEditedAddress() {
        val currentAddresses = usuarioAtual?.endereco
        if (usuarioAtual == null || currentAddresses == null ||
            editingPosition < 0 || editingPosition >= currentAddresses.size
        ) {
            Toast.makeText(context, "Erro: Dados inválidos para edição.", Toast.LENGTH_SHORT).show()
            return
        }

        if (editCep.text.toString().isEmpty() || editLogradouro.text.toString().isEmpty()) {
            Toast.makeText(context, "CEP e Logradouro são obrigatórios.", Toast.LENGTH_SHORT).show()
            return
        }

        val editedAddress = hashMapOf<String, Any>(
            "cep" to editCep.text.toString(),
            "logradouro" to editLogradouro.text.toString(),
            "numero" to editNumero.text.toString(),
            "complemento" to editComplemento.text.toString(),
            "bairro" to editBairro.text.toString(),
            "cidade" to editCidade.text.toString(),
            "estado" to editEstado.text.toString()
        )

        val updatedAddresses = ArrayList(currentAddresses)
        updatedAddresses[editingPosition] = editedAddress

        db.collection("usuarios")
            .document(usuarioAtual!!.uid!!)
            .update("endereco", updatedAddresses)
            .addOnSuccessListener {
                Toast.makeText(context, "Endereço atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                clearAddressFields()
                listener?.onAddressUpdated()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Erro ao atualizar endereço: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearAddressFields() {
        editCep.setText("")
        editLogradouro.setText("")
        editNumero.setText("")
        editComplemento.setText("")
        editBairro.setText("")
        editCidade.setText("")
        editEstado.setText("")
    }

    fun resetForm() {
        isEditMode = false
        editingPosition = -1
        clearAddressFields()
        buttonAddAddress.visibility = View.VISIBLE
        buttonSaveChanges.visibility = View.GONE
    }
}
