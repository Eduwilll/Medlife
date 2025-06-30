package medlife.com.br.helper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

public class OrderManagerTest {
    @Mock
    FirebaseFirestore mockFirestore;
    private MockedStatic<UsuarioFirebase> mockedUsuarioFirebase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        if (mockedUsuarioFirebase != null) {
            mockedUsuarioFirebase.close();
        }
    }

    @Test
    public void testSaveOrder_NullUserId_ReturnsNull() {
        // Arrange
        Order order = new Order();
        // Mock UsuarioFirebase.getIdUsuario() to return null
        mockedUsuarioFirebase = mockStatic(UsuarioFirebase.class);
        mockedUsuarioFirebase.when(UsuarioFirebase::getIdUsuario).thenReturn(null);

        // Act
        Task<Void> result = OrderManager.saveOrder(order);

        // Assert
        assertNull(result);
    }
} 