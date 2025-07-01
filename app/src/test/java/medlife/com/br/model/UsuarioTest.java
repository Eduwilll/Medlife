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
    public void testSetAndGetGenero() {
        usuario.setGenero("Masculino");
        assertEquals("Masculino", usuario.getGenero());
    }

    @Test
    public void testSetAndGetOtherGender() {
        usuario.setOtherGender("Não-binário");
        assertEquals("Não-binário", usuario.getOtherGender());
    }

    @Test
    public void testSetAndGetPhoneNumber() {
        usuario.setPhoneNumber("11999999999");
        assertEquals("11999999999", usuario.getPhoneNumber());
    }

    @Test
    public void testSetAndGetDateOfBirth() {
        usuario.setDateOfBirth("1990-01-01");
        assertEquals("1990-01-01", usuario.getDateOfBirth());
    }

    @Test
    public void testSetAndGetCpf() {
        usuario.setCpf("12345678901");
        assertEquals("12345678901", usuario.getCpf());
    }

    @Test
    public void testSetAndGetEndereco() {
        List<Map<String, Object>> enderecos = new ArrayList<>();
        Map<String, Object> endereco = new HashMap<>();
        endereco.put("street", "Rua Teste");
        endereco.put("city", "São Paulo");
        enderecos.add(endereco);
        usuario.setEndereco(enderecos);
        assertEquals(enderecos, usuario.getEndereco());
    }

    @Test
    public void testSetAndGetOrders() {
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        order.setOrderId("order1");
        orders.add(order);
        usuario.setOrders(orders);
        assertEquals(orders, usuario.getOrders());
    }

    @Test
    public void testSetAndGetCriadoEm() {
        Timestamp now = Timestamp.now();
        usuario.setCriadoEm(now);
        assertEquals(now, usuario.getCriadoEm());
    }

    @Test
    public void testSetAndGetLastLogin() {
        Timestamp now = Timestamp.now();
        usuario.setLastLogin(now);
        assertEquals(now, usuario.getLastLogin());
    }

    @Test
    public void testSetAndGetEnderecoPrincipal() {
        usuario.setEnderecoPrincipal(1);
        assertEquals(1, usuario.getEnderecoPrincipal());
    }

    @Test
    public void testDefaultConstructor() {
        Usuario newUsuario = new Usuario();
        assertNotNull(newUsuario);
        assertNull(newUsuario.getUid());
        assertNull(newUsuario.getNome());
        assertNull(newUsuario.getEmail());
        assertEquals(0, newUsuario.getEnderecoPrincipal());
    }

    @Test
    public void testParameterizedConstructor() {
        Usuario newUsuario = new Usuario("uid123", "John Doe", "john@example.com");
        assertEquals("uid123", newUsuario.getUid());
        assertEquals("John Doe", newUsuario.getNome());
        assertEquals("john@example.com", newUsuario.getEmail());
    }

    @Test
    public void testParameterizedConstructor_NullValues() {
        Usuario newUsuario = new Usuario(null, null, null);
        assertNull(newUsuario.getUid());
        assertNull(newUsuario.getNome());
        assertNull(newUsuario.getEmail());
    }
} 