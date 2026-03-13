package medlife.com.br.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UsuarioTest {
    private lateinit var usuario: Usuario

    @Before
    fun setUp() {
        usuario = Usuario()
    }

    @Test fun testSetAndGetUid() { usuario.uid = "uid123"; assertEquals("uid123", usuario.uid) }
    @Test fun testSetAndGetNome() { usuario.nome = "John Doe"; assertEquals("John Doe", usuario.nome) }
    @Test fun testSetAndGetEmail() { usuario.email = "john@example.com"; assertEquals("john@example.com", usuario.email) }
    @Test fun testSetAndGetGenero() { usuario.genero = "Masculino"; assertEquals("Masculino", usuario.genero) }
    @Test fun testSetAndGetOtherGender() { usuario.otherGender = "Não-binário"; assertEquals("Não-binário", usuario.otherGender) }
    @Test fun testSetAndGetPhoneNumber() { usuario.phoneNumber = "11999999999"; assertEquals("11999999999", usuario.phoneNumber) }
    @Test fun testSetAndGetDateOfBirth() { usuario.dateOfBirth = "1990-01-01"; assertEquals("1990-01-01", usuario.dateOfBirth) }
    @Test fun testSetAndGetCpf() { usuario.cpf = "12345678901"; assertEquals("12345678901", usuario.cpf) }

    @Test
    fun testSetAndGetEndereco() {
        val enderecos: List<Map<String, Any>> = listOf(mapOf("street" to "Rua Teste", "city" to "São Paulo"))
        usuario.endereco = enderecos
        assertEquals(enderecos, usuario.endereco)
    }

    @Test
    fun testSetAndGetOrders() {
        val orders = listOf(Order().apply { orderId = "order1" })
        usuario.orders = orders
        assertEquals(orders, usuario.orders)
    }

    @Test
    fun testSetAndGetCriadoEm() {
        val now = Timestamp.now()
        usuario.criadoEm = now
        assertEquals(now, usuario.criadoEm)
    }

    @Test
    fun testSetAndGetLastLogin() {
        val now = Timestamp.now()
        usuario.lastLogin = now
        assertEquals(now, usuario.lastLogin)
    }

    @Test
    fun testSetAndGetEnderecoPrincipal() {
        usuario.enderecoPrincipal = 1
        assertEquals(1, usuario.enderecoPrincipal)
    }

    @Test
    fun testDefaultConstructor() {
        val newUsuario = Usuario()
        assertNotNull(newUsuario)
        assertNull(newUsuario.uid)
        assertNull(newUsuario.nome)
        assertNull(newUsuario.email)
        assertEquals(0, newUsuario.enderecoPrincipal)
    }

    @Test
    fun testParameterizedConstructor() {
        val newUsuario = Usuario().apply {
            uid = "uid123"
            nome = "John Doe"
            email = "john@example.com"
        }
        assertEquals("uid123", newUsuario.uid)
        assertEquals("John Doe", newUsuario.nome)
        assertEquals("john@example.com", newUsuario.email)
    }

    @Test
    fun testParameterizedConstructor_NullValues() {
        val newUsuario = Usuario().apply {
            uid = null
            nome = null
            email = null
        }
        assertNull(newUsuario.uid)
        assertNull(newUsuario.nome)
        assertNull(newUsuario.email)
    }
}
