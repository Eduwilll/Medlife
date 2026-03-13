package medlife.com.br.model

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartItemTest {
    private lateinit var product: Product
    private lateinit var cartItem: CartItem

    @Before
    fun setUp() {
        product = Product(1, "Produto", "Descricao", "R\$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, null)
        cartItem = CartItem(product, 2)
    }

    @Test
    fun testGetProduct() {
        assertEquals(product, cartItem.product)
    }

    @Test
    fun testGetAndSetQuantity() {
        cartItem.quantity = 5
        assertEquals(5, cartItem.quantity)
    }
}
