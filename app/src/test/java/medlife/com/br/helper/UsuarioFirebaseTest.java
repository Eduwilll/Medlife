package medlife.com.br.helper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

public class UsuarioFirebaseTest {
    @Mock
    FirebaseAuth mockAuth;
    @Mock
    FirebaseUser mockUser;
    private MockedStatic<ConfiguracaoFirebase> mockedConfiguracaoFirebase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        if (mockedConfiguracaoFirebase != null) {
            mockedConfiguracaoFirebase.close();
        }
    }

    @Test
    public void testGetIdUsuario_ReturnsUid() {
        // Arrange
        mockedConfiguracaoFirebase = mockStatic(ConfiguracaoFirebase.class);
        mockedConfiguracaoFirebase.when(ConfiguracaoFirebase::getFirebaseAutenticacao).thenReturn(mockAuth);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUid123");

        // Act
        String uid = UsuarioFirebase.getIdUsuario();

        // Assert
        assertEquals("testUid123", uid);
    }

    @Test
    public void testGetUsuarioAtual_ReturnsUser() {
        // Arrange
        mockedConfiguracaoFirebase = mockStatic(ConfiguracaoFirebase.class);
        mockedConfiguracaoFirebase.when(ConfiguracaoFirebase::getFirebaseAutenticacao).thenReturn(mockAuth);
        when(mockAuth.getCurrentUser()).thenReturn(mockUser);

        // Act
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();

        // Assert
        assertEquals(mockUser, user);
    }
} 