package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class FarmaciaTest {
    private Farmacia farmacia;

    @Before
    public void setUp() {
        farmacia = new Farmacia("1", "Farmacia Teste", "Rua 1");
    }

    @Test
    public void testGetAndSetId() {
        farmacia.setId("2");
        assertEquals("2", farmacia.getId());
    }

    @Test
    public void testGetAndSetName() {
        farmacia.setName("Nova Farmacia");
        assertEquals("Nova Farmacia", farmacia.getName());
    }

    @Test
    public void testGetAndSetLocation() {
        farmacia.setLocation("Rua 2");
        assertEquals("Rua 2", farmacia.getLocation());
    }
} 