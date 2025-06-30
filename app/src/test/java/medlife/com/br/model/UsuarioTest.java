package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.google.firebase.Timestamp;
import java.util.*;

public class UsuarioTest {
    private Usuario usuario;

    @Before
    public void setUp() {
        usuario = new Usuario();
    }

    @Test
    public void testSetAndGetUid() {
        usuario.setUid("uid123");
        assertEquals("uid123", usuario.getUid());
    }

    @Test
    public void testSetAndGetNome() {
        usuario.setNome("John Doe");
        assertEquals("John Doe", usuario.getNome());
    }

    @Test
    public void testSetAndGetEmail() {
        usuario.setEmail("john@example.com");
        assertEquals("john@example.com", usuario.getEmail());
    }

    @Test
    public void testSetAndGetLastLogin() {
        Timestamp now = Timestamp.now();
        usuario.setLastLogin(now);
        assertEquals(now, usuario.getLastLogin());
    }

    // Add more tests for other fields as needed
} 