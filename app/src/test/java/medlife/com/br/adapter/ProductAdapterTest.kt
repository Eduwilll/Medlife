package medlife.com.br.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.model.Farmacia
import medlife.com.br.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ProductAdapterTest {
    @Mock lateinit var mockContext: Context
    @Mock lateinit var mockView: View
    @Mock lateinit var mockParent: ViewGroup
    @Mock lateinit var mockImageView: ImageView
    @Mock lateinit var mockTextView: TextView
    @Mock lateinit var mockSharedPreferences: SharedPreferences
    @Mock lateinit var mockEditor: SharedPreferences.Editor
    @Mock lateinit var mockFirestore: FirebaseFirestore

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var product: Product
    private lateinit var farmacia: Farmacia

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        farmacia = Farmacia("1", "Farmacia Teste", "Rua 1")
        product = Product(1, "Produto", "Descricao", "R\$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, farmacia)

        productList = mutableListOf(product)
        productAdapter = ProductAdapter(mockContext, productList, false)
    }

    @Test
    fun testConstructor_WithFavoriteMode() {
        val adapter = ProductAdapter(mockContext, productList, true)
        assertEquals(1, adapter.itemCount)
    }

    @Test
    fun testGetItemCount() {
        assertEquals(1, productAdapter.itemCount)
    }

    @Test
    fun testSetOnItemClickListener() {
        val listener = ProductAdapter.OnItemClickListener { _ -> }
        productAdapter.setOnItemClickListener(listener)
    }

    @Test
    fun testProductViewHolderConstructor() {
        `when`(mockView.findViewById<ImageView>(R.id.imageProduct)).thenReturn(mockImageView)
        `when`(mockView.findViewById<TextView>(R.id.textProductName)).thenReturn(mockTextView)
        `when`(mockView.findViewById<TextView>(R.id.textProductDesc)).thenReturn(mockTextView)
        `when`(mockView.findViewById<TextView>(R.id.textProductPrice)).thenReturn(mockTextView)
        `when`(mockView.findViewById<ImageView>(R.id.addButton)).thenReturn(mockImageView)
        `when`(mockView.findViewById<ImageView>(R.id.favoriteIcon)).thenReturn(mockImageView)

        val holder = ProductAdapter.ProductViewHolder(mockView)

        assertNotNull(holder.imageProduct)
        assertNotNull(holder.textProductName)
        assertNotNull(holder.textProductDesc)
        assertNotNull(holder.textProductPrice)
        assertNotNull(holder.addButton)
        assertNotNull(holder.favoriteIcon)
    }

    @Test fun testAddFavoriteToFirestore_NullUserId() { /* Skipped: requires Firebase runtime */ }
    @Test fun testAddFavoriteToFirestore_NullProduct() { /* Skipped: requires Firebase runtime */ }
    @Test fun testRemoveFavoriteFromFirestore_NullUserId() { /* Skipped: requires Firebase runtime */ }
    @Test fun testRemoveFavoriteFromFirestore_NullProduct() { /* Skipped: requires Firebase runtime */ }
}
