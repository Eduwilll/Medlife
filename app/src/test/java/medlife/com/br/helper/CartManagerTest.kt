package medlife.com.br.helper

import medlife.com.br.model.CartItem
import medlife.com.br.model.Farmacia
import medlife.com.br.model.Order
import medlife.com.br.model.Product
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mockStatic

class CartManagerTest {
    @Mock lateinit var mockProduct: Product
    @Mock lateinit var mockFarmacia: Farmacia

    private lateinit var product: Product
    private lateinit var farmacia: Farmacia
    private lateinit var cartItem: CartItem

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        CartManager.clearCart()

        farmacia = Farmacia("1", "Farmacia Teste", "Rua 1")
        product = Product(1, "Produto", "Descricao", "R\$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, farmacia)
        cartItem = CartItem(product, 2)
    }

    @Test
    fun testAddProduct() {
        CartManager.addProduct(product, 2)
        assertEquals(1, CartManager.getCartItems().size)
        assertEquals(product, CartManager.getCartItems()[0].product)
    }

    @Test
    fun testAddProduct_ExistingProduct() {
        CartManager.addProduct(product, 2)
        CartManager.addProduct(product, 3)
        assertEquals(1, CartManager.getCartItems().size)
        assertEquals(5, CartManager.getCartItems()[0].quantity)
    }

    @Test
    fun testRemoveProduct() {
        CartManager.addProduct(product, 2)
        val item = CartManager.getCartItems()[0]
        CartManager.removeProduct(item)
        assertEquals(0, CartManager.getCartItems().size)
    }

    @Test
    fun testUpdateQuantity() {
        CartManager.addProduct(product, 2)
        val item = CartManager.getCartItems()[0]
        CartManager.updateQuantity(item, 5)
        assertEquals(5, item.quantity)
    }

    @Test
    fun testUpdateQuantity_Zero() {
        CartManager.addProduct(product, 2)
        val item = CartManager.getCartItems()[0]
        CartManager.updateQuantity(item, 0)
        assertEquals(0, CartManager.getCartItems().size)
    }

    @Test
    fun testUpdateQuantity_Negative() {
        CartManager.addProduct(product, 2)
        val item = CartManager.getCartItems()[0]
        CartManager.updateQuantity(item, -1)
        assertEquals(0, CartManager.getCartItems().size)
    }

    @Test
    fun testClearCart() {
        CartManager.addProduct(product, 2)
        CartManager.clearCart()
        assertEquals(0, CartManager.getCartItems().size)
    }

    @Test
    fun testGetTotalPrice() {
        CartManager.addProduct(product, 2)
        assertEquals(20.0, CartManager.getTotalPrice(), 0.01)
    }

    @Test
    fun testGetTotalPrice_EmptyCart() {
        assertEquals(0.0, CartManager.getTotalPrice(), 0.01)
    }

    @Test
    fun testGetTotalPrice_InvalidPriceFormat() {
        val invalidProduct = Product(1, "Invalid", "Desc", "Invalid Price", "Cat", "Brand", Product.TARJA_AMARELA, farmacia)
        CartManager.addProduct(invalidProduct, 1)
        assertEquals(0.0, CartManager.getTotalPrice(), 0.01)
    }

    @Test
    fun testCreateOrderFromCart_NullUserId() {
        CartManager.addProduct(product, 2)
        mockStatic(UsuarioFirebase::class).use { mockedStatic ->
            mockedStatic.`when`<String?> { UsuarioFirebase.getIdUsuario() }.thenReturn(null)
            val order = CartManager.createOrderFromCart()
            assertNull(order)
        }
    }

    @Test
    fun testCreateOrderFromCart_EmptyCart() {
        mockStatic(UsuarioFirebase::class).use { mockedStatic ->
            mockedStatic.`when`<String?> { UsuarioFirebase.getIdUsuario() }.thenReturn("testUserId")
            val order = CartManager.createOrderFromCart()
            assertNull(order)
        }
    }

    @Test
    fun testCreateOrderFromCart_Success() {
        CartManager.addProduct(product, 2)
        mockStatic(UsuarioFirebase::class).use { mockedStatic ->
            mockedStatic.`when`<String?> { UsuarioFirebase.getIdUsuario() }.thenReturn("testUserId")
            val order = CartManager.createOrderFromCart()
            assertNotNull(order)
            assertEquals("testUserId", order!!.userId)
            assertEquals(20.0, order.totalPrice, 0.01)
            assertEquals("ORDER_CONFIRMED", order.status)
            assertEquals(1, order.items!!.size)
        }
    }

    @Test
    fun testGetCartItems() {
        CartManager.addProduct(product, 2)
        val items = CartManager.getCartItems()
        assertEquals(1, items.size)
        assertEquals(product, items[0].product)
    }
}
