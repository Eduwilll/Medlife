package medlife.com.br.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import medlife.com.br.R
import medlife.com.br.helper.CartManager
import medlife.com.br.model.CartItem
import medlife.com.br.model.Farmacia

class CartAdapter(
    private val context: Context,
    cartItems: List<CartItem>,
    private val listener: CartListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var displayList: MutableList<Any> = buildDisplayList(cartItems)

    interface CartListener {
        fun onCartUpdated()
    }

    private fun buildDisplayList(cartItems: List<CartItem>): MutableList<Any> {
        val grouped = LinkedHashMap<Farmacia, MutableList<CartItem>>()
        for (item in cartItems) {
            val product = item.product
            if (product != null) {
                val farmacia = product.farmacia
                if (farmacia != null) {
                    if (!grouped.containsKey(farmacia)) {
                        grouped[farmacia] = mutableListOf()
                    }
                    grouped[farmacia]?.add(item)
                }
            }
        }
        val result = mutableListOf<Any>()
        for ((key, value) in grouped) {
            result.add(key)
            result.addAll(value)
        }
        return result
    }

    override fun getItemViewType(position: Int): Int {
        return if (displayList[position] is Farmacia) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_cart_pharmacy_header, parent, false)
            PharmacyHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
            CartViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            val farmacia = displayList[position] as Farmacia
            val headerHolder = holder as PharmacyHeaderViewHolder
            headerHolder.textPharmacyName.text = farmacia.name
            headerHolder.textPharmacyLocation.text = farmacia.location
            
            val nameLower = farmacia.name?.lowercase() ?: ""
            if (nameLower.contains("são paulo")) {
                headerHolder.imagePharmacyLogo.setImageResource(R.drawable.mock_drogariasaopaulo)
            } else if (nameLower.contains("drogasil")) {
                headerHolder.imagePharmacyLogo.setImageResource(R.drawable.mock_logo_drogasil_2048)
            } else {
                headerHolder.imagePharmacyLogo.setImageResource(R.drawable.logo)
            }
        } else {
            val cartItem = displayList[position] as CartItem
            val itemHolder = holder as CartViewHolder
            val product = cartItem.product
            
            if (product != null) {
                itemHolder.productImage.setImageResource(product.image)
                itemHolder.productName.text = product.name
                itemHolder.productPrice.text = product.price
            }
            itemHolder.quantityText.text = cartItem.quantity.toString()

            itemHolder.plusButton.setOnClickListener {
                CartManager.updateQuantity(cartItem, cartItem.quantity + 1)
                notifyDataSetChanged()
                listener.onCartUpdated()
            }

            itemHolder.minusButton.setOnClickListener {
                CartManager.updateQuantity(cartItem, cartItem.quantity - 1)
                notifyDataSetChanged()
                listener.onCartUpdated()
            }

            itemHolder.deleteButton.setOnClickListener {
                CartManager.removeProduct(cartItem)
                notifyDataSetChanged()
                listener.onCartUpdated()
            }
        }
    }

    override fun getItemCount(): Int {
        return displayList.size
    }

    fun updateCartItems(cartItems: List<CartItem>) {
        this.displayList = buildDisplayList(cartItems)
        notifyDataSetChanged()
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val quantityText: TextView = itemView.findViewById(R.id.quantity_text)
        val plusButton: ImageView = itemView.findViewById(R.id.plus_button)
        val minusButton: ImageView = itemView.findViewById(R.id.minus_button)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
    }

    class PharmacyHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textPharmacyName: TextView = itemView.findViewById(R.id.textPharmacyName)
        val textPharmacyLocation: TextView = itemView.findViewById(R.id.textPharmacyLocation)
        val imagePharmacyLogo: ImageView = itemView.findViewById(R.id.imagePharmacyLogo)
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
