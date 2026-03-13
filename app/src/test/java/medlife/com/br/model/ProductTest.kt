package medlife.com.br.model

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ProductTest {
    private lateinit var product: Product
    private lateinit var farmacia: Farmacia

    @Before
    fun setUp() {
        farmacia = Farmacia("1", "Farmacia Teste", "Rua 1")
        product = Product(1, "Produto", "Descricao", "R\$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, farmacia)
    }

    @Test fun testGetImage() { assertEquals(1, product.image) }
    @Test fun testGetName() { assertEquals("Produto", product.name) }
    @Test fun testGetDescription() { assertEquals("Descricao", product.description) }
    @Test fun testGetPrice() { assertEquals("R\$10,00", product.price) }
    @Test fun testGetCategory() { assertEquals("Categoria", product.category) }
    @Test fun testGetBrand() { assertEquals("Marca", product.brand) }
    @Test fun testGetTarja() { assertEquals(Product.TARJA_AMARELA, product.tarja) }
    @Test fun testGetFarmacia() { assertEquals(farmacia, product.farmacia) }

    @Test
    fun testSetFarmacia() {
        val newFarmacia = Farmacia("2", "Nova Farmacia", "Rua 2")
        product.farmacia = newFarmacia
        assertEquals(newFarmacia, product.farmacia)
    }

    @Test
    fun testTarjaConstants() {
        assertEquals("SEM_TARJA", Product.TARJA_SEM_TARJA)
        assertEquals("AMARELA", Product.TARJA_AMARELA)
        assertEquals("VERMELHA_SEM_RETENCAO", Product.TARJA_VERMELHA_SEM_RETENCAO)
        assertEquals("VERMELHA_COM_RETENCAO", Product.TARJA_VERMELHA_COM_RETENCAO)
        assertEquals("PRETA", Product.TARJA_PRETA)
    }

    @Test fun testDescribeContents() { assertEquals(0, product.describeContents()) }

    @Test fun testParcelableImplementation() { /* Skipped: requires Android runtime */ }

    @Test
    fun testNewArray() {
        val products = Product.CREATOR.newArray(5)
        assertEquals(5, products.size)
        for (p in products) assertNull(p)
    }

    @Test
    fun testConstructorWithNullFarmacia() {
        val productWithNullFarmacia = Product(1, "Produto", "Descricao", "R\$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, null)
        assertNull(productWithNullFarmacia.farmacia)
    }

    @Test
    fun testConstructorWithAllTarjaTypes() {
        val productSemTarja = Product(1, "Produto1", "Desc", "R\$10,00", "Cat", "Brand", Product.TARJA_SEM_TARJA, farmacia)
        val productVermelha = Product(2, "Produto2", "Desc", "R\$10,00", "Cat", "Brand", Product.TARJA_VERMELHA_SEM_RETENCAO, farmacia)
        val productPreta = Product(3, "Produto3", "Desc", "R\$10,00", "Cat", "Brand", Product.TARJA_PRETA, farmacia)

        assertEquals(Product.TARJA_SEM_TARJA, productSemTarja.tarja)
        assertEquals(Product.TARJA_VERMELHA_SEM_RETENCAO, productVermelha.tarja)
        assertEquals(Product.TARJA_PRETA, productPreta.tarja)
    }
}
