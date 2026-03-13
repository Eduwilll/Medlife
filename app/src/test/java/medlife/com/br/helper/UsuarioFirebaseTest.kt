package medlife.com.br.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.MockedStatic
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

class UsuarioFirebaseTest {
    @Mock lateinit var mockAuth: FirebaseAuth
    @Mock lateinit var mockUser: FirebaseUser
    @Mock lateinit var mockFirestore: FirebaseFirestore
    @Mock lateinit var mockProfileChangeRequest: UserProfileChangeRequest
    @Mock lateinit var mockBuilder: UserProfileChangeRequest.Builder

    private var mockedConfiguracaoFirebase: MockedStatic<ConfiguracaoFirebase>? = null
    private var mockedFirebaseFirestore: MockedStatic<FirebaseFirestore>? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDown() {
        mockedConfiguracaoFirebase?.close()
        mockedConfiguracaoFirebase = null
        mockedFirebaseFirestore?.close()
        mockedFirebaseFirestore = null
    }

    @Test
    fun testGetIdUsuario_ReturnsUid() {
        mockStatic(ConfiguracaoFirebase::class.java).use { mockedStatic ->
            mockedStatic.`when`<FirebaseAuth> { ConfiguracaoFirebase.getFirebaseAutenticacao() }.thenReturn(mockAuth)
            `when`(mockAuth.currentUser).thenReturn(mockUser)
            `when`(mockUser.uid).thenReturn("testUid123")

            val uid = UsuarioFirebase.getIdUsuario()
            assertEquals("testUid123", uid)
        }
    }

    @Test
    fun testGetIdUsuario_NullUser_ReturnsNull() {
        mockStatic(ConfiguracaoFirebase::class.java).use { mockedStatic ->
            mockedStatic.`when`<FirebaseAuth> { ConfiguracaoFirebase.getFirebaseAutenticacao() }.thenReturn(mockAuth)
            `when`(mockAuth.currentUser).thenReturn(null)

            val uid = UsuarioFirebase.getIdUsuario()
            assertNull(uid)
        }
    }

    @Test
    fun testGetUsuarioAtual_ReturnsUser() {
        mockStatic(ConfiguracaoFirebase::class.java).use { mockedStatic ->
            mockedStatic.`when`<FirebaseAuth> { ConfiguracaoFirebase.getFirebaseAutenticacao() }.thenReturn(mockAuth)
            `when`(mockAuth.currentUser).thenReturn(mockUser)

            val user = UsuarioFirebase.getUsuarioAtual()
            assertNotNull(user)
            assertEquals(mockUser, user)
        }
    }

    @Test
    fun testGetUsuarioAtual_NullUser_ReturnsNull() {
        mockStatic(ConfiguracaoFirebase::class.java).use { mockedStatic ->
            mockedStatic.`when`<FirebaseAuth> { ConfiguracaoFirebase.getFirebaseAutenticacao() }.thenReturn(mockAuth)
            `when`(mockAuth.currentUser).thenReturn(null)

            val user = UsuarioFirebase.getUsuarioAtual()
            assertNull(user)
        }
    }

    @Test fun testAtualizarLastLogin_ValidUser() { /* Skipped: requires Firebase runtime */ }

    @Test
    fun testAtualizarLastLogin_NullUser() {
        mockedConfiguracaoFirebase = mockStatic(ConfiguracaoFirebase::class.java)
        mockedConfiguracaoFirebase!!.`when`<FirebaseAuth> { ConfiguracaoFirebase.getFirebaseAutenticacao() }.thenReturn(mockAuth)
        `when`(mockAuth.currentUser).thenReturn(null)

        UsuarioFirebase.atualizarLastLogin() // Should not throw
    }

    @Test fun testAtualizarLastLogin_WithUserId_ValidUserId() { /* Skipped: requires Firebase runtime */ }

    @Test
    fun testAtualizarLastLogin_WithUserId_NullUserId() {
        UsuarioFirebase.atualizarLastLogin(null) // Should not throw
    }

    @Test
    fun testAtualizarLastLogin_WithUserId_EmptyUserId() {
        UsuarioFirebase.atualizarLastLogin("") // Should not throw
    }

    @Test fun testAtualizarLastLoginSeguro_ValidUserId() { /* Skipped: requires Firebase runtime */ }

    @Test
    fun testAtualizarLastLoginSeguro_NullUserId() {
        UsuarioFirebase.atualizarLastLoginSeguro(null) // Should not throw
    }

    @Test
    fun testAtualizarLastLoginSeguro_EmptyUserId() {
        UsuarioFirebase.atualizarLastLoginSeguro("") // Should not throw
    }
}
