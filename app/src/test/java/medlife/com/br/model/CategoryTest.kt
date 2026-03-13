package medlife.com.br.model

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CategoryTest {
    private lateinit var category: Category

    @Before
    fun setUp() {
        category = Category(123, "Teste")
    }

    @Test
    fun testSetAndGetName() {
        category.name = "Medicamentos"
        assertEquals("Medicamentos", category.name)
    }
}
