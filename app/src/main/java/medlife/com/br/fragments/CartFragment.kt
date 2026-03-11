package medlife.com.br.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.activity.HomeActivity
import medlife.com.br.activity.OrderSuccessActivity
import medlife.com.br.adapter.CartAdapter
import medlife.com.br.fragments.profile.ProfileAddressesFragment
import medlife.com.br.helper.CartManager
import medlife.com.br.helper.OrderManager
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.model.CartItem
import medlife.com.br.model.Order
import medlife.com.br.model.Product
import medlife.com.br.model.Usuario
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment(), CartAdapter.CartListener {

    private lateinit var recyclerCartItems: RecyclerView
    private var cartAdapter: CartAdapter? = null
    private var cartItems: List<CartItem> = emptyList()
    private lateinit var textTotal: TextView
    private lateinit var buttonCheckout: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var textEmptyCart: TextView
    private lateinit var imageEmptyCart: ImageView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var layoutMainContent: LinearLayout
    private lateinit var layoutPrescriptionWarning: LinearLayout
    private lateinit var textDeliveryAddress: TextView
    private lateinit var textDeliveryAddressTitle: TextView
    private lateinit var layoutDeliveryAddress: LinearLayout
    private lateinit var layoutNoAddress: LinearLayout
    private lateinit var textSubtotal: TextView
    private lateinit var textDeliveryFee: TextView
    private lateinit var textDiscount: TextView
    private lateinit var cardDeliveryImmediate: LinearLayout
    private lateinit var cardDeliveryStorePickup: LinearLayout
    private lateinit var cardDeliveryScheduled: LinearLayout
    private lateinit var textDeliveryImmediateTitle: TextView
    private lateinit var textDeliveryStorePickupTitle: TextView
    private lateinit var textDeliveryScheduledTitle: TextView
    private lateinit var buttonAddAddress: Button
    private lateinit var buttonAddAddressNoAddress: Button
    private lateinit var textAddressWarning: TextView

    var currentDeliveryFee = 7.00
        private set
    var currentDiscount = 0.0
        private set
    private var selectedDeliveryOption = "immediate"
    private var backStackListener: FragmentManager.OnBackStackChangedListener? = null
    private var hasAddress = false
    private val uploadedPrescriptions = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        recyclerCartItems = view.findViewById(R.id.recyclerCartItems)
        textTotal = view.findViewById(R.id.textTotal)
        buttonCheckout = view.findViewById(R.id.buttonCheckout)
        db = FirebaseFirestore.getInstance()
        textEmptyCart = view.findViewById(R.id.textEmptyCart)
        imageEmptyCart = view.findViewById(R.id.imageEmptyCart)
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        layoutMainContent = view.findViewById(R.id.layoutMainContent)
        layoutPrescriptionWarning = view.findViewById(R.id.layoutPrescriptionWarning)
        textDeliveryAddress = view.findViewById(R.id.textDeliveryAddress)
        textDeliveryAddressTitle = view.findViewById(R.id.textDeliveryAddressTitle)
        layoutDeliveryAddress = view.findViewById(R.id.layoutDeliveryAddress)
        layoutNoAddress = view.findViewById(R.id.layoutNoAddress)
        textSubtotal = view.findViewById(R.id.textSubtotal)
        textDeliveryFee = view.findViewById(R.id.textDeliveryFee)
        textDiscount = view.findViewById(R.id.textDiscount)
        cardDeliveryImmediate = view.findViewById(R.id.cardDeliveryImmediate)
        cardDeliveryStorePickup = view.findViewById(R.id.cardDeliveryStorePickup)
        cardDeliveryScheduled = view.findViewById(R.id.cardDeliveryScheduled)
        textDeliveryImmediateTitle = view.findViewById(R.id.textDeliveryImmediateTitle)
        textDeliveryStorePickupTitle = view.findViewById(R.id.textDeliveryStorePickupTitle)
        textDeliveryScheduledTitle = view.findViewById(R.id.textDeliveryScheduledTitle)
        buttonAddAddress = view.findViewById(R.id.buttonAddAddress)
        buttonAddAddressNoAddress = view.findViewById(R.id.buttonAddAddressNoAddress)
        textAddressWarning = view.findViewById(R.id.textAddressWarning)

        setupCart()
        loadUserPrincipalAddress()
        setupDeliveryOptions()
        setupAddressButtons()

        buttonCheckout.setOnClickListener {
            if (!hasAddress) {
                Toast.makeText(context, "Você precisa adicionar um endereço para continuar", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            buttonCheckout.isEnabled = false
            buttonCheckout.text = "Processando..."

            val newOrder = CartManager.getInstance().createOrderFromCart()
            if (newOrder != null) {
                populateOrderDetails(newOrder)
            } else {
                Toast.makeText(context, "Erro: Não foi possível criar o pedido", Toast.LENGTH_SHORT).show()
                buttonCheckout.isEnabled = true
                buttonCheckout.text = "Finalizar Compra"
            }
        }

        val buttonUploadPrescription: Button = view.findViewById(R.id.buttonUploadPrescription)
        buttonUploadPrescription.setOnClickListener {
            layoutMainContent.visibility = View.GONE
            val fragmentContainer: View = view.findViewById(R.id.fragment_container)
            fragmentContainer.visibility = View.VISIBLE

            val fragment = UploadPrescriptionFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack("prescription_upload")
            transaction.commit()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.let { fm ->
            backStackListener = FragmentManager.OnBackStackChangedListener {
                if (fm.backStackEntryCount == 0) {
                    if (::layoutMainContent.isInitialized) layoutMainContent.visibility = View.VISIBLE
                    view.findViewById<View>(R.id.fragment_container)?.visibility = View.GONE
                } else {
                    if (::layoutMainContent.isInitialized) layoutMainContent.visibility = View.GONE
                    view.findViewById<View>(R.id.fragment_container)?.visibility = View.VISIBLE
                }
            }
            backStackListener?.let { fm.addOnBackStackChangedListener(it) }
        }
    }

    private fun loadUserPrincipalAddress() {
        val userId = UsuarioFirebase.idUsuario
        if (userId != null) {
            db.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val usuario = documentSnapshot.toObject(Usuario::class.java)
                        val enderecoList = usuario?.endereco
                        if (usuario != null && !enderecoList.isNullOrEmpty()) {
                            val principalIndex = usuario.enderecoPrincipal
                            if (principalIndex in enderecoList.indices) {
                                @Suppress("UNCHECKED_CAST")
                                displayAddress(enderecoList[principalIndex] as Map<String, Any>)
                            } else {
                                @Suppress("UNCHECKED_CAST")
                                displayAddress(enderecoList[0] as Map<String, Any>)
                            }
                        } else {
                            layoutDeliveryAddress.visibility = View.GONE
                            layoutNoAddress.visibility = View.VISIBLE
                            hasAddress = false
                        }
                    } else {
                        layoutDeliveryAddress.visibility = View.GONE
                        layoutNoAddress.visibility = View.VISIBLE
                        hasAddress = false
                    }
                    updateCheckoutButtonState()
                }
                .addOnFailureListener {
                    layoutDeliveryAddress.visibility = View.GONE
                    layoutNoAddress.visibility = View.VISIBLE
                    hasAddress = false
                    updateCheckoutButtonState()
                }
        } else {
            layoutDeliveryAddress.visibility = View.GONE
            layoutNoAddress.visibility = View.VISIBLE
            hasAddress = false
            updateCheckoutButtonState()
        }
    }

    private fun displayAddress(address: Map<String, Any>) {
        val logradouro = address["logradouro"] as? String ?: ""
        val numero = address["numero"] as? String ?: ""
        val complemento = address["complemento"] as? String ?: ""
        val bairro = address["bairro"] as? String ?: ""
        val cidade = address["cidade"] as? String ?: ""
        val estado = address["estado"] as? String ?: ""
        val cep = address["cep"] as? String ?: ""

        val addressLine1 = "$logradouro, $numero${if (complemento.isNotEmpty()) " - $complemento" else ""}"
        val addressLine2 = "$bairro, $cidade - $estado, $cep"

        textDeliveryAddress.text = "$addressLine1\n$addressLine2"
        layoutDeliveryAddress.visibility = View.VISIBLE
        layoutNoAddress.visibility = View.GONE
        hasAddress = true
        updateCheckoutButtonState()
    }

    private fun setupCart() {
        cartItems = CartManager.getInstance().cartItems

        if (cartItems.isEmpty()) {
            layoutEmptyState.visibility = View.VISIBLE
            layoutMainContent.visibility = View.GONE
        } else {
            layoutEmptyState.visibility = View.GONE
            layoutMainContent.visibility = View.VISIBLE

            if (cartAdapter == null) {
                cartAdapter = context?.let { CartAdapter(it, cartItems, this) }
                recyclerCartItems.layoutManager = LinearLayoutManager(context)
                recyclerCartItems.adapter = cartAdapter
            } else {
                cartAdapter?.updateCartItems(cartItems)
            }
        }
        updateSummary()
        updatePrescriptionWarning()
        updateCheckoutButtonState()
    }

    private fun updatePrescriptionWarning() {
        val needsPrescription = cartItems.any { item ->
            val tarja = item.product?.tarja
            tarja == Product.TARJA_PRETA || tarja == Product.TARJA_VERMELHA_SEM_RETENCAO || tarja == Product.TARJA_VERMELHA_COM_RETENCAO
        }
        if (::layoutPrescriptionWarning.isInitialized) {
            layoutPrescriptionWarning.visibility = if (needsPrescription) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        setupCart()
        loadUserPrincipalAddress()

        if (hasAddress && CartManager.getInstance().cartItems.isNotEmpty()) {
            showAddressAddedFeedback()
        }
    }

    private fun showAddressAddedFeedback() {
        view?.let {
            Snackbar.make(
                it,
                "Endereço adicionado com sucesso! Você pode continuar com a compra.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCartUpdated() {
        updateSummary()
        if (CartManager.getInstance().cartItems.isEmpty()) {
            setupCart()
        } else {
            cartAdapter?.updateCartItems(CartManager.getInstance().cartItems)
            updateCheckoutButtonState()
        }
        updateCartBadge()
    }

    private fun updateCartBadge() {
        (activity as? HomeActivity)?.updateCartBadge()
    }

    private fun setupDeliveryOptions() {
        updateDeliveryCardSelection()
        cardDeliveryImmediate.setOnClickListener {
            selectedDeliveryOption = "immediate"
            currentDeliveryFee = 7.00
            updateDeliveryCardSelection()
            updateSummary()
        }
        cardDeliveryStorePickup.setOnClickListener {
            selectedDeliveryOption = "pickup"
            currentDeliveryFee = 0.00
            updateDeliveryCardSelection()
            updateSummary()
        }
        cardDeliveryScheduled.setOnClickListener {
            selectedDeliveryOption = "scheduled"
            currentDeliveryFee = 7.00
            updateDeliveryCardSelection()
            updateSummary()
        }
    }

    private fun updateDeliveryCardSelection() {
        cardDeliveryImmediate.setBackgroundResource(if (selectedDeliveryOption == "immediate") R.drawable.bg_delivery_option_selected else R.drawable.bg_delivery_option_unselected)
        cardDeliveryStorePickup.setBackgroundResource(if (selectedDeliveryOption == "pickup") R.drawable.bg_delivery_option_selected else R.drawable.bg_delivery_option_unselected)
        cardDeliveryScheduled.setBackgroundResource(if (selectedDeliveryOption == "scheduled") R.drawable.bg_delivery_option_selected else R.drawable.bg_delivery_option_unselected)
        
        val color = 0xFF1E56A0.toInt()
        textDeliveryImmediateTitle.setTextColor(color)
        textDeliveryStorePickupTitle.setTextColor(color)
        textDeliveryScheduledTitle.setTextColor(color)
    }

    private fun updateSummary() {
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        
        val subtotal = CartManager.getInstance().totalPrice
        textSubtotal.text = format.format(subtotal)
        textDeliveryFee.text = format.format(currentDeliveryFee)
        textDiscount.text = format.format(currentDiscount)
        
        val total = subtotal + currentDeliveryFee - currentDiscount
        textTotal.text = format.format(total)
        
        updateCheckoutButtonState()
    }

    private fun updateCheckoutButtonState() {
        val cartNotEmpty = CartManager.getInstance().cartItems.isNotEmpty()
        val canCheckout = hasAddress && cartNotEmpty

        buttonCheckout.isEnabled = canCheckout
        buttonCheckout.alpha = if (canCheckout) 1.0f else 0.5f

        when {
            !hasAddress && cartNotEmpty -> {
                buttonCheckout.text = "Adicione um endereço para continuar"
                textAddressWarning.visibility = View.VISIBLE
                buttonAddAddress.visibility = View.VISIBLE
                buttonAddAddressNoAddress.visibility = View.VISIBLE
            }
            hasAddress && cartNotEmpty -> {
                buttonCheckout.text = "Finalizar Compra"
                textAddressWarning.visibility = View.GONE
                buttonAddAddress.visibility = View.GONE
                buttonAddAddressNoAddress.visibility = View.GONE
            }
            else -> {
                buttonCheckout.text = "Finalizar Compra"
                textAddressWarning.visibility = View.GONE
                buttonAddAddress.visibility = View.GONE
                buttonAddAddressNoAddress.visibility = View.GONE
            }
        }
    }

    fun applyCoupon(discountValue: Double) {
        this.currentDiscount = discountValue
        updateSummary()
    }

    fun removeCoupon() {
        this.currentDiscount = 0.0
        updateSummary()
    }

    fun applyCouponCode(couponCode: String): Boolean {
        if ("DESCONTO10" == couponCode) {
            val subtotal = CartManager.getInstance().totalPrice
            this.currentDiscount = subtotal * 0.10
            updateSummary()
            return true
        } else if ("FREEGRATIS" == couponCode) {
            this.currentDiscount = currentDeliveryFee
            updateSummary()
            return true
        }
        return false
    }

    fun getCurrentTotal(): Double {
        val subtotal = CartManager.getInstance().totalPrice
        return subtotal + currentDeliveryFee - currentDiscount
    }

    fun addPrescription(prescriptionId: String) {
        if (!uploadedPrescriptions.contains(prescriptionId)) {
            uploadedPrescriptions.add(prescriptionId)
            println("Prescription added: $prescriptionId")
            println("Total prescriptions: ${uploadedPrescriptions.size}")
        }
    }

    fun getUploadedPrescriptions(): List<String> {
        return uploadedPrescriptions
    }

    fun hasUploadedPrescriptions(): Boolean {
        return uploadedPrescriptions.isNotEmpty()
    }

    private fun populateOrderDetails(newOrder: Order) {
        newOrder.deliveryOption = selectedDeliveryOption
        newOrder.deliveryFee = currentDeliveryFee
        newOrder.subtotal = CartManager.getInstance().totalPrice
        newOrder.discountAmount = currentDiscount
        newOrder.totalPrice = getCurrentTotal()
        newOrder.paymentMethod = "Pendente"
        newOrder.paymentStatus = "pending"

        val needsPrescription = cartItems.any { item ->
            val tarja = item.product?.tarja
            tarja == Product.TARJA_PRETA || tarja == Product.TARJA_VERMELHA_SEM_RETENCAO || tarja == Product.TARJA_VERMELHA_COM_RETENCAO
        }
        newOrder.isRequiresPrescription = needsPrescription

        if (needsPrescription && hasUploadedPrescriptions()) {
            newOrder.prescriptionIds = ArrayList(uploadedPrescriptions)
            newOrder.prescriptionStatus = "pending"
            newOrder.prescriptionUploadDate = Timestamp.now()
            println("Setting prescription IDs: $uploadedPrescriptions")
        } else if (needsPrescription && !hasUploadedPrescriptions()) {
            newOrder.prescriptionStatus = "missing"
            println("Order requires prescription but none uploaded")
        }

        if (layoutDeliveryAddress.visibility == View.VISIBLE) {
            val userId = UsuarioFirebase.idUsuario
            if (userId != null) {
                db.collection("usuarios").document(userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val usuario = documentSnapshot.toObject(Usuario::class.java)
                            val enderecoList = usuario?.endereco
                            if (usuario != null && !enderecoList.isNullOrEmpty()) {
                                val principalIndex = usuario.enderecoPrincipal
                                val address = if (principalIndex in enderecoList.indices) {
                                    enderecoList[principalIndex]
                                } else {
                                    enderecoList[0]
                                }
                                @Suppress("UNCHECKED_CAST")
                                newOrder.deliveryAddress = address as Map<String, Any>?
                                println("Setting delivery address: $address")
                                saveOrderWithAddress(newOrder)
                            } else {
                                println("No addresses available for user")
                                saveOrderWithAddress(newOrder)
                            }
                        } else {
                            println("User document doesn't exist")
                            saveOrderWithAddress(newOrder)
                        }
                    }
                    .addOnFailureListener { e ->
                        println("Error loading user data: ${e.message}")
                        saveOrderWithAddress(newOrder)
                    }
            } else {
                println("User not authenticated")
                saveOrderWithAddress(newOrder)
            }
        } else {
            println("No delivery address UI visible")
            saveOrderWithAddress(newOrder)
        }

        if (selectedDeliveryOption == "immediate" || selectedDeliveryOption == "scheduled") {
            newOrder.estimatedDeliveryTime = Timestamp.now()
        }
    }

    private fun saveOrderWithAddress(newOrder: Order) {
        println("Saving order with delivery address: ${newOrder.deliveryAddress}")

        OrderManager.saveOrder(newOrder)?.addOnSuccessListener {
            CartManager.getInstance().clearCart()
            val intent = Intent(activity, OrderSuccessActivity::class.java)
            startActivity(intent)
            Toast.makeText(context, "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show()
        }?.addOnFailureListener { e ->
            Toast.makeText(context, "Falha ao realizar o pedido: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            buttonCheckout.isEnabled = true
            buttonCheckout.text = "Finalizar Compra"
        }
    }

    private fun setupAddressButtons() {
        buttonAddAddress.setOnClickListener { navigateToAddressesFragment() }
        buttonAddAddressNoAddress.setOnClickListener { navigateToAddressesFragment() }
    }

    private fun navigateToAddressesFragment() {
        activity?.supportFragmentManager?.let { fm ->
            val addressesFragment = ProfileAddressesFragment()
            fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            fm.beginTransaction()
                .replace(R.id.contentFrame, addressesFragment)
                .addToBackStack("addresses")
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backStackListener?.let {
            parentFragmentManager.removeOnBackStackChangedListener(it)
        }
    }
}
