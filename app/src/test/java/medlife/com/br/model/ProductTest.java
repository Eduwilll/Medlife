package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ProductTest {
    private Product product;
    private Farmacia farmacia;

    @Before
    public void setUp() {
        farmacia = new Farmacia("1", "Farmacia Teste", "Rua 1");
        product = new Product(1, "Produto", "Descricao", "R$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, farmacia);
    }

    @Test
    public void testGetName() {
        assertEquals("Produto", product.getName());
    }

    @Test
    public void testGetFarmacia() {
        assertEquals(farmacia, product.getFarmacia());
    }

    // Add more tests for other getters as needed
} 