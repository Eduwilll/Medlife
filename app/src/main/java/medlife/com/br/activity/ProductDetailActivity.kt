package medlife.com.br.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import medlife.com.br.R
import medlife.com.br.adapter.ProductAdapter
import medlife.com.br.helper.CartManager
import medlife.com.br.model.Product

class ProductDetailActivity : AppCompatActivity() {

    private var quantity = 1
    private lateinit var quantityText: TextView
    private var isFavorite = false
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val productImage: ImageView = findViewById(R.id.product_image)
        val productName: TextView = findViewById(R.id.product_name)
        val productSubtitle: TextView = findViewById(R.id.product_subtitle)
        val productPrice: TextView = findViewById(R.id.product_price)
        val backArrow: ImageView = findViewById(R.id.back_arrow)
        val minusButton: ImageView = findViewById(R.id.minus_button)
        val plusButton: ImageView = findViewById(R.id.plus_button)
        quantityText = findViewById(R.id.quantity_text)
        val addToCartButton: Button = findViewById(R.id.add_to_cart_button)
        val pharmacyLogo: ImageView = findViewById(R.id.imagePharmacyLogo)
        val pharmacyName: TextView = findViewById(R.id.textPharmacyName)
        val pharmacyLocation: TextView = findViewById(R.id.textPharmacyLocation)
        val favoriteIcon: ImageView = findViewById(R.id.favorite_icon)

        @Suppress("DEPRECATION")
        val product = intent.getParcelableExtra<Product>("product")

        if (product != null) {
            productImage.setImageResource(product.image)
            productName.text = product.name
            productSubtitle.text = product.description
            productPrice.text = product.price

            val farmacia = product.farmacia
            if (farmacia != null) {
                pharmacyName.text = farmacia.name
                pharmacyLocation.text = farmacia.location
                
                val lowerName = farmacia.name?.lowercase() ?: ""
                if (lowerName.contains("são paulo")) {
                    pharmacyLogo.setImageResource(R.drawable.mock_drogariasaopaulo)
                } else if (lowerName.contains("drogasil")) {
                    pharmacyLogo.setImageResource(R.drawable.mock_logo_drogasil_2048)
                } else {
                    pharmacyLogo.setImageResource(R.drawable.mock_logo_drogasil_2048)
                }
            }

            prefs = getSharedPreferences("favorites", Context.MODE_PRIVATE)
            isFavorite = prefs.getBoolean(product.name, false)
            updateFavoriteIcon(favoriteIcon)
        }

        backArrow.setOnClickListener { finish() }

        minusButton.setOnClickListener {
            if (quantity > 1) {
                quantity--
                quantityText.text = quantity.toString()
            }
        }

        plusButton.setOnClickListener {
            quantity++
            quantityText.text = quantity.toString()
        }

        addToCartButton.setOnClickListener {
            if (product != null) {
                CartManager.addProduct(product, quantity)
                Toast.makeText(this, "Adicionado ao carrinho", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        favoriteIcon.setOnClickListener {
            if (product != null && product.name != null) {
                isFavorite = !isFavorite
                prefs.edit().putBoolean(product.name, isFavorite).apply()
                updateFavoriteIcon(favoriteIcon)
                
                if (isFavorite) {
                    ProductAdapter.addFavoriteToFirestore(product)
                } else {
                    ProductAdapter.removeFavoriteFromFirestore(product)
                }
            }
        }
    }

    private fun updateFavoriteIcon(icon: ImageView) {
        val color = if (isFavorite) {
            resources.getColor(R.color.blue_500, null)
        } else {
            resources.getColor(R.color.colorGray, null)
        }
        icon.setColorFilter(color)
    }
}
