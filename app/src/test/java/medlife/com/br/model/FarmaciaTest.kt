package medlife.com.br.model

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FarmaciaTest {
    private lateinit var farmacia: Farmacia

    @Before
    fun setUp() {
        farmacia = Farmacia("1", "Farmacia Teste", "Rua 1")
    }

    @Test
    fun testGetAndSetId() {
        farmacia.id = "2"
        assertEquals("2", farmacia.id)
    }

    @Test
    fun testGetAndSetName() {
        farmacia.name = "Nova Farmacia"
        assertEquals("Nova Farmacia", farmacia.name)
    }

    @Test
    fun testGetAndSetLocation() {
        farmacia.location = "Rua 2"
        assertEquals("Rua 2", farmacia.location)
    }
}
