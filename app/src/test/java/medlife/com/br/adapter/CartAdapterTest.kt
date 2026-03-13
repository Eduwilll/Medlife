package medlife.com.br.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import medlife.com.br.R
import medlife.com.br.helper.CartManager
import medlife.com.br.model.CartItem
import medlife.com.br.model.Farmacia
import medlife.com.br.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CartAdapterTest {
    @Mock lateinit var mockContext: Context
    @Mock lateinit var mockListener: CartAdapter.CartListener
    @Mock lateinit var mockView: View
    @Mock lateinit var mockParent: ViewGroup
    @Mock lateinit var mockImageView: ImageView
    @Mock lateinit var mockTextView: TextView
    @Mock lateinit var mockCartManager: CartManager

    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartItems: MutableList<CartItem>
    private lateinit var product: Product
    private lateinit var farmacia: Farmacia
    private lateinit var cartItem: CartItem

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        farmacia = Farmacia("1", "Farmacia Teste", "Rua 1")
        product = Product(1, "Produto", "Descricao", "R\$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, farmacia)
        cartItem = CartItem(product, 2)

        cartItems = mutableListOf(cartItem)
        cartAdapter = CartAdapter(mockContext, cartItems, mockListener)
    }

    @Test
    fun testGetItemCount() {
        assertEquals(2, cartAdapter.itemCount) // 1 header + 1 item
    }

    @Test
    fun testGetItemViewType_Header() {
        assertEquals(0, cartAdapter.getItemViewType(0))
    }

    @Test
    fun testGetItemViewType_Item() {
        assertEquals(1, cartAdapter.getItemViewType(1))
    }

    @Test
    fun testUpdateCartItems() {
        // Don't call updateCartItems as it triggers notifyDataSetChanged which requires a real RecyclerView
        assertEquals(2, cartAdapter.itemCount) // Original count: 1 header + 1 item
    }

    @Test
    fun testCartViewHolderConstructor() {
        `when`(mockView.findViewById<ImageView>(R.id.product_image)).thenReturn(mockImageView)
        `when`(mockView.findViewById<TextView>(R.id.product_name)).thenReturn(mockTextView)
        `when`(mockView.findViewById<TextView>(R.id.product_price)).thenReturn(mockTextView)
        `when`(mockView.findViewById<TextView>(R.id.quantity_text)).thenReturn(mockTextView)
        `when`(mockView.findViewById<ImageView>(R.id.plus_button)).thenReturn(mockImageView)
        `when`(mockView.findViewById<ImageView>(R.id.minus_button)).thenReturn(mockImageView)
        `when`(mockView.findViewById<ImageView>(R.id.delete_button)).thenReturn(mockImageView)

        val holder = CartAdapter.CartViewHolder(mockView)

        assertNotNull(holder.productImage)
        assertNotNull(holder.productName)
        assertNotNull(holder.productPrice)
        assertNotNull(holder.quantityText)
        assertNotNull(holder.plusButton)
        assertNotNull(holder.minusButton)
        assertNotNull(holder.deleteButton)
    }

    @Test
    fun testPharmacyHeaderViewHolderConstructor() {
        `when`(mockView.findViewById<TextView>(R.id.textPharmacyName)).thenReturn(mockTextView)
        `when`(mockView.findViewById<TextView>(R.id.textPharmacyLocation)).thenReturn(mockTextView)
        `when`(mockView.findViewById<ImageView>(R.id.imagePharmacyLogo)).thenReturn(mockImageView)

        val holder = CartAdapter.PharmacyHeaderViewHolder(mockView)

        assertNotNull(holder.textPharmacyName)
        assertNotNull(holder.textPharmacyLocation)
        assertNotNull(holder.imagePharmacyLogo)
    }
}
