package medlife.com.br.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.activity.ProductDetailActivity
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.model.Product

class ProductAdapter(
    private val context: Context?,
    private val productList: MutableList<Product>,
    private val favoriteMode: Boolean = false
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view: View = if (favoriteMode) {
            LayoutInflater.from(context).inflate(R.layout.item_favorite_product, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        }
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.imageProduct.setImageResource(product.image)
        holder.textProductName.text = product.name
        holder.textProductPrice?.text = product.price
        
        if (!favoriteMode) {
            holder.textProductDesc?.text = product.description
        }

        holder.itemView.setOnClickListener {
            if (onItemClickListener != null) {
                onItemClickListener?.onItemClick(product)
            } else if (!favoriteMode && context != null) {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("product", product)
                context.startActivity(intent)
            }
        }

        if (favoriteMode && holder.favoriteIcon != null) {
            holder.favoriteIcon.visibility = View.VISIBLE
            holder.favoriteIcon.setOnClickListener {
                val adapterPosition = holder.adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < productList.size) {
                    val prefs = context?.getSharedPreferences("favorites", Context.MODE_PRIVATE)
                    prefs?.edit()?.putBoolean(product.name, false)?.apply()
                    
                    removeFavoriteFromFirestore(product)
                    
                    productList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                    notifyItemRangeChanged(adapterPosition, productList.size)
                }
            }
        } else if (holder.favoriteIcon != null) {
            holder.favoriteIcon.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.imageProduct)
        val textProductName: TextView = itemView.findViewById(R.id.textProductName)
        val textProductDesc: TextView? = itemView.findViewById(R.id.textProductDesc)
        val textProductPrice: TextView? = itemView.findViewById(R.id.textProductPrice)
        val addButton: ImageView? = itemView.findViewById(R.id.addButton)
        val favoriteIcon: ImageView? = itemView.findViewById(R.id.favoriteIcon)
    }

    companion object {
        fun addFavoriteToFirestore(product: Product?) {
            val userId = UsuarioFirebase.idUsuario
            if (userId != null && product != null && product.name != null) {
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(userId)
                    .collection("favoritosProdutos")
                    .document(product.name!!)
                    .set(product)
            }
        }

        fun removeFavoriteFromFirestore(product: Product?) {
            val userId = UsuarioFirebase.idUsuario
            if (userId != null && product != null && product.name != null) {
                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(userId)
                    .collection("favoritosProdutos")
                    .document(product.name!!)
                    .delete()
            }
        }
    }
}
