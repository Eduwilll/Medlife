package medlife.com.br.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.model.Usuario

class ProfileAddressesFragment : Fragment() {
    private lateinit var recyclerAddresses: RecyclerView
    private lateinit var buttonAddAddress: Button
    private lateinit var buttonToggleAddAddress: Button
    private lateinit var scrollViewAddAddress: ScrollView
    private lateinit var textEmpty: TextView
    private lateinit var editCep: EditText
    private lateinit var editLogradouro: EditText
    private lateinit var editNumero: EditText
    private lateinit var editComplemento: EditText
    private lateinit var editBairro: EditText
    private lateinit var editCidade: EditText
    private lateinit var editEstado: EditText
    private lateinit var buttonSaveChanges: Button
    private lateinit var db: FirebaseFirestore
    private var usuarioAtual: Usuario? = null
    private lateinit var addressAdapter: AddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_addresses, container, false)

        db = FirebaseFirestore.getInstance()

        recyclerAddresses = view.findViewById(R.id.recyclerAddresses)
        buttonAddAddress = view.findViewById(R.id.buttonAddAddress)
        buttonToggleAddAddress = view.findViewById(R.id.buttonToggleAddAddress)
        scrollViewAddAddress = view.findViewById(R.id.scrollViewAddAddress)
        textEmpty = view.findViewById(R.id.textEmpty)
        editCep = view.findViewById(R.id.editCep)
        editLogradouro = view.findViewById(R.id.editLogradouro)
        editNumero = view.findViewById(R.id.editNumero)
        editComplemento = view.findViewById(R.id.editComplemento)
        editBairro = view.findViewById(R.id.editBairro)
        editCidade = view.findViewById(R.id.editCidade)
        editEstado = view.findViewById(R.id.editEstado)
        buttonSaveChanges = view.findViewById(R.id.buttonSaveChanges)

        recyclerAddresses.layoutManager = LinearLayoutManager(context)
        addressAdapter = AddressAdapter(mutableListOf(), usuarioAtual)
        recyclerAddresses.adapter = addressAdapter

        loadUserAddresses()

        buttonAddAddress.setOnClickListener { addNewAddress() }
        buttonSaveChanges.setOnClickListener { saveEditedAddress() }

        buttonToggleAddAddress.setOnClickListener {
            if (scrollViewAddAddress.visibility == View.GONE) {
                scrollViewAddAddress.visibility = View.VISIBLE
                buttonToggleAddAddress.setText(R.string.ocultar_formul_rio)
                buttonAddAddress.visibility = View.VISIBLE
                buttonSaveChanges.visibility = View.GONE
            } else {
                scrollViewAddAddress.visibility = View.GONE
                buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o)
                buttonAddAddress.visibility = View.VISIBLE
                buttonSaveChanges.visibility = View.GONE
                clearAddressFields()
            }
        }

        return view
    }

    private fun loadUserAddresses() {
        val userId = UsuarioFirebase.idUsuario
        if (userId == null) {
            Toast.makeText(context, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        println("Loading addresses for user: $userId")

        db.collection("usuarios")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    usuarioAtual = documentSnapshot.toObject(Usuario::class.java)
                    if (usuarioAtual != null && !usuarioAtual!!.endereco.isNullOrEmpty()) {
                        println("Found ${usuarioAtual!!.endereco!!.size} addresses for user")
                        addressAdapter.updateAddresses(usuarioAtual!!.endereco, usuarioAtual)
                        recyclerAddresses.visibility = View.VISIBLE
                        textEmpty.visibility = View.GONE
                    } else {
                        println("No addresses found for user")
                        recyclerAddresses.visibility = View.GONE
                        textEmpty.visibility = View.VISIBLE
                    }
                } else {
                    println("User document doesn't exist")
                    recyclerAddresses.visibility = View.GONE
                    textEmpty.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                println("Error loading addresses: ${e.message}")
                Toast.makeText(context, "Erro ao carregar endereços: ${e.message}", Toast.LENGTH_SHORT).show()
                recyclerAddresses.visibility = View.GONE
                textEmpty.visibility = View.VISIBLE
            }
    }

    private fun addNewAddress() {
        if (usuarioAtual != null) {
            val novoEndereco = hashMapOf<String, Any>(
                "cep" to editCep.text.toString(),
                "logradouro" to editLogradouro.text.toString(),
                "numero" to editNumero.text.toString(),
                "complemento" to editComplemento.text.toString(),
                "bairro" to editBairro.text.toString(),
                "cidade" to editCidade.text.toString(),
                "estado" to editEstado.text.toString()
            )

            if (editCep.text.toString().isEmpty() || editLogradouro.text.toString().isEmpty()) {
                Toast.makeText(context, "CEP e Logradouro são obrigatórios.", Toast.LENGTH_SHORT).show()
                return
            }

            println("Adding new address: $novoEndereco")

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
                    println("Error checking user document: ${e.message}")
                    Toast.makeText(context, "Erro ao verificar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addAddressToExistingUser(novoEndereco: Map<String, Any>) {
        db.collection("usuarios")
            .document(usuarioAtual!!.uid!!)
            .update("endereco", FieldValue.arrayUnion(novoEndereco))
            .addOnSuccessListener {
                println("Address added successfully to Firestore")
                Toast.makeText(context, "Endereço adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                clearAddressFields()
                loadUserAddresses()
                scrollViewAddAddress.visibility = View.GONE
                buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o)
                buttonAddAddress.visibility = View.VISIBLE
                buttonSaveChanges.visibility = View.GONE

                showAddressAddedSnackbar()
            }
            .addOnFailureListener { e ->
                println("Error adding address: ${e.message}")
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
                println("User created with address successfully")
                Toast.makeText(context, "Usuário criado com endereço!", Toast.LENGTH_SHORT).show()
                clearAddressFields()
                loadUserAddresses()
                scrollViewAddAddress.visibility = View.GONE
                buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o)
                buttonAddAddress.visibility = View.VISIBLE
                buttonSaveChanges.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                println("Error creating user with address: ${e.message}")
                Toast.makeText(context, "Erro ao criar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveEditedAddress() {
        val tag = buttonSaveChanges.tag
        if (tag == null || tag !is Int) return
        
        val editingPosition = tag

        if (usuarioAtual != null && usuarioAtual!!.endereco != null && editingPosition >= 0 && editingPosition < usuarioAtual!!.endereco!!.size) {
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

            val currentAddresses = ArrayList(usuarioAtual!!.endereco!!)
            currentAddresses[editingPosition] = editedAddress

            db.collection("usuarios")
                .document(usuarioAtual!!.uid!!)
                .update("endereco", currentAddresses)
                .addOnSuccessListener {
                    Toast.makeText(context, "Endereço atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    clearAddressFields()
                    loadUserAddresses()
                    scrollViewAddAddress.visibility = View.GONE
                    buttonToggleAddAddress.setText(R.string.adicionar_novo_endere_o)
                    buttonAddAddress.visibility = View.VISIBLE
                    buttonSaveChanges.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao atualizar endereço: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Erro ao salvar alterações. Endereço não encontrado ou usuário inválido.", Toast.LENGTH_SHORT).show()
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

    private inner class AddressAdapter(
        private val addresses: MutableList<Map<String, Any>>,
        private var usuarioAtual: Usuario?
    ) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

        @SuppressLint("NotifyDataSetChanged")
        fun updateAddresses(newAddresses: List<Map<String, Any>>?, usuario: Usuario?) {
            this.addresses.clear()
            if (newAddresses != null) {
                this.addresses.addAll(newAddresses)
            }
            this.usuarioAtual = usuario
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
            return AddressViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
            val address = addresses[position]
            holder.bind(address, position, usuarioAtual)
        }

        override fun getItemCount(): Int {
            return addresses.size
        }

        inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textAddressLine1: TextView = itemView.findViewById(R.id.textAddressLine1)
            private val textAddressLine2: TextView = itemView.findViewById(R.id.textAddressLine2)
            private val buttonEditAddress: ImageButton = itemView.findViewById(R.id.buttonEditAddress)
            private val buttonDeleteAddress: ImageButton = itemView.findViewById(R.id.buttonDeleteAddress)
            private val buttonSetPrincipal: Button = itemView.findViewById(R.id.buttonSetPrincipal)
            private val textPrincipal: TextView = itemView.findViewById(R.id.textPrincipal)

            fun bind(address: Map<String, Any>, position: Int, usuarioAtual: Usuario?) {
                val logradouro = address["logradouro"] as? String ?: ""
                val numero = address["numero"] as? String ?: ""
                val complemento = address["complemento"] as? String ?: ""
                val bairro = address["bairro"] as? String ?: ""
                val cidade = address["cidade"] as? String ?: ""
                val estado = address["estado"] as? String ?: ""
                val cep = address["cep"] as? String ?: ""

                val complStr = if (complemento.isNotEmpty()) " - $complemento" else ""
                val addressLine1 = "$logradouro, $numero$complStr"
                val addressLine2 = "$bairro, $cidade - $estado, $cep"

                textAddressLine1.text = addressLine1
                textAddressLine2.text = addressLine2

                val isPrincipal = usuarioAtual != null && usuarioAtual.enderecoPrincipal == position
                textPrincipal.visibility = if (isPrincipal) View.VISIBLE else View.GONE
                buttonSetPrincipal.visibility = if (isPrincipal) View.GONE else View.VISIBLE

                itemView.setBackgroundResource(if (isPrincipal) R.drawable.bg_address_principal_card else android.R.color.transparent)

                buttonSetPrincipal.setOnClickListener { setPrincipalAddress(position) }

                buttonEditAddress.setOnClickListener {
                    editAddress(address, adapterPosition)
                }

                buttonDeleteAddress.setOnClickListener {
                    deleteAddress(address, adapterPosition)
                }
            }
        }
    }

    private fun editAddress(address: Map<String, Any>, position: Int) {
        scrollViewAddAddress.visibility = View.VISIBLE
        buttonToggleAddAddress.setText(R.string.ocultar_formul_rio)

        editCep.setText(address["cep"] as? String ?: "")
        editLogradouro.setText(address["logradouro"] as? String ?: "")
        editNumero.setText(address["numero"] as? String ?: "")
        editComplemento.setText(address["complemento"] as? String ?: "")
        editBairro.setText(address["bairro"] as? String ?: "")
        editCidade.setText(address["cidade"] as? String ?: "")
        editEstado.setText(address["estado"] as? String ?: "")

        buttonAddAddress.visibility = View.GONE
        buttonSaveChanges.visibility = View.VISIBLE
        buttonSaveChanges.tag = position
    }

    private fun deleteAddress(address: Map<String, Any>, position: Int) {
        if (usuarioAtual != null && usuarioAtual!!.endereco != null) {
            val currentAddresses = ArrayList(usuarioAtual!!.endereco!!)
            if (position in currentAddresses.indices) {
                currentAddresses.removeAt(position)

                val currentPrincipal = usuarioAtual!!.enderecoPrincipal
                if (currentPrincipal == position) {
                    usuarioAtual!!.enderecoPrincipal = if (currentAddresses.isEmpty()) -1 else 0
                } else if (currentPrincipal > position) {
                    usuarioAtual!!.enderecoPrincipal = currentPrincipal - 1
                }

                db.collection("usuarios")
                    .document(usuarioAtual!!.uid!!)
                    .update("endereco", currentAddresses, "enderecoPrincipal", usuarioAtual!!.enderecoPrincipal)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Endereço removido com sucesso!", Toast.LENGTH_SHORT).show()
                        loadUserAddresses()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Erro ao remover endereço: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(context, "Erro: Usuário ou endereços inválidos.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setPrincipalAddress(position: Int) {
        if (usuarioAtual != null) {
            db.collection("usuarios")
                .document(usuarioAtual!!.uid!!)
                .update("enderecoPrincipal", position)
                .addOnSuccessListener {
                    Toast.makeText(context, "Endereço principal atualizado!", Toast.LENGTH_SHORT).show()
                    loadUserAddresses()
                    addressAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao atualizar endereço principal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showAddressAddedSnackbar() {
        view?.let {
            Snackbar.make(
                it,
                "Endereço adicionado! Voltar ao carrinho?",
                Snackbar.LENGTH_LONG
            ).setAction("Voltar") {
                activity?.onBackPressed()
            }.show()
        }
    }
}
