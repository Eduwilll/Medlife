package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class CartItemTest {
    private Product product;
    private CartItem cartItem;

    @Before
    public void setUp() {
        product = new Product(1, "Produto", "Descricao", "R$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, null);
        cartItem = new CartItem(product, 2);
    }

    @Test
    public void testGetProduct() {
        assertEquals(product, cartItem.getProduct());
    }

    @Test
    public void testGetAndSetQuantity() {
        cartItem.setQuantity(5);
        assertEquals(5, cartItem.getQuantity());
    }
} 