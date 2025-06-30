package medlife.com.br.helper;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import medlife.com.br.model.Product;
import medlife.com.br.model.CartItem;

public class CartManagerTest {
    private CartManager cartManager;
    private Product product;

    @Before
    public void setUp() {
        cartManager = CartManager.getInstance();
        cartManager.clearCart(); // Ensure clean state
        product = new Product(1, "Produto", "Descricao", "R$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, null);
    }

    @Test
    public void testAddProduct() {
        cartManager.addProduct(product, 2);
        assertEquals(1, cartManager.getCartItems().size());
        assertEquals(product, cartManager.getCartItems().get(0).getProduct());
    }

    @Test
    public void testRemoveProduct() {
        cartManager.addProduct(product, 2);
        CartItem item = cartManager.getCartItems().get(0);
        cartManager.removeProduct(item);
        assertEquals(0, cartManager.getCartItems().size());
    }

    @Test
    public void testUpdateQuantity() {
        cartManager.addProduct(product, 2);
        CartItem item = cartManager.getCartItems().get(0);
        cartManager.updateQuantity(item, 5);
        assertEquals(5, item.getQuantity());
    }

    @Test
    public void testClearCart() {
        cartManager.addProduct(product, 2);
        cartManager.clearCart();
        assertEquals(0, cartManager.getCartItems().size());
    }
} 