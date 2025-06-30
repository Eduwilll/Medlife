package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CategoryTest {
    private Category category;

    @Before
    public void setUp() {
        category = new Category(123, "Teste");
    }

    @Test
    public void testSetAndGetName() {
        category.setName("Medicamentos");
        assertEquals("Medicamentos", category.getName());
    }
} 