package medlife.com.br.helper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.annotation.SuppressLint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import medlife.com.br.model.Product;
import medlife.com.br.model.CartItem;
import medlife.com.br.model.Order;
import medlife.com.br.model.Farmacia;
import medlife.com.br.helper.UsuarioFirebase;
import java.util.List;

public class CartManagerTest {
    @Mock
    Product mockProduct;
    @Mock
    Farmacia mockFarmacia;

    private CartManager cartManager;
    private Product product;
    private Farmacia farmacia;
    private CartItem cartItem;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cartManager = CartManager.getInstance();
        cartManager.clearCart(); // Ensure clean state
        
        farmacia = new Farmacia("1", "Farmacia Teste", "Rua 1");
        product = new Product(1, "Produto", "Descricao", "R$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, farmacia);
        cartItem = new CartItem(product, 2);
    }

    @Test
    public void testAddProduct() {
        cartManager.addProduct(product, 2);
        assertEquals(1, cartManager.getCartItems().size());
        assertEquals(product, cartManager.getCartItems().get(0).getProduct());
    }

    @Test
    public void testAddProduct_ExistingProduct() {
        cartManager.addProduct(product, 2);
        cartManager.addProduct(product, 3);
        assertEquals(1, cartManager.getCartItems().size());
        assertEquals(5, cartManager.getCartItems().get(0).getQuantity());
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
    public void testUpdateQuantity_Zero() {
        cartManager.addProduct(product, 2);
        CartItem item = cartManager.getCartItems().get(0);
        cartManager.updateQuantity(item, 0);
        assertEquals(0, cartManager.getCartItems().size());
    }

    @Test
    public void testUpdateQuantity_Negative() {
        cartManager.addProduct(product, 2);
        CartItem item = cartManager.getCartItems().get(0);
        cartManager.updateQuantity(item, -1);
        assertEquals(0, cartManager.getCartItems().size());
    }

    @Test
    public void testClearCart() {
        cartManager.addProduct(product, 2);
        cartManager.clearCart();
        assertEquals(0, cartManager.getCartItems().size());
    }

    @Test
    public void testGetTotalPrice() {
        cartManager.addProduct(product, 2);
        assertEquals(20.0, cartManager.getTotalPrice(), 0.01);
    }

    @Test
    public void testGetTotalPrice_EmptyCart() {
        assertEquals(0.0, cartManager.getTotalPrice(), 0.01);
    }

    @Test
    public void testGetTotalPrice_InvalidPriceFormat() {
        Product invalidProduct = new Product(1, "Invalid", "Desc", "Invalid Price", "Cat", "Brand", Product.TARJA_AMARELA, farmacia);
        cartManager.addProduct(invalidProduct, 1);
        assertEquals(0.0, cartManager.getTotalPrice(), 0.01);
    }

    @Test
    public void testCreateOrderFromCart_NullUserId() {
        cartManager.addProduct(product, 2);
        try (MockedStatic<UsuarioFirebase> mockedStatic = mockStatic(UsuarioFirebase.class)) {
            mockedStatic.when(UsuarioFirebase::getIdUsuario).thenReturn(null);
            
            Order order = cartManager.createOrderFromCart();
            assertNull(order);
        }
    }

    @SuppressLint("CheckResult")
    @Test
    public void testCreateOrderFromCart_EmptyCart() {
        try (MockedStatic<UsuarioFirebase> mockedStatic = mockStatic(UsuarioFirebase.class)) {
            mockedStatic.when(UsuarioFirebase::getIdUsuario).thenReturn("testUserId");
            
            Order order = cartManager.createOrderFromCart();
            assertNull(order);
        }
    }

    @Test
    public void testCreateOrderFromCart_Success() {
        cartManager.addProduct(product, 2);
        try (MockedStatic<UsuarioFirebase> mockedStatic = mockStatic(UsuarioFirebase.class)) {
            mockedStatic.when(UsuarioFirebase::getIdUsuario).thenReturn("testUserId");
            
            Order order = cartManager.createOrderFromCart();
            assertNotNull(order);
            assertEquals("testUserId", order.getUserId());
            assertEquals(20.0, order.getTotalPrice(), 0.01);
            assertEquals("ORDER_CONFIRMED", order.getStatus());
            assertEquals(1, order.getItems().size());
        }
    }

    @Test
    public void testGetCartItems() {
        cartManager.addProduct(product, 2);
        List<CartItem> items = cartManager.getCartItems();
        assertEquals(1, items.size());
        assertEquals(product, items.get(0).getProduct());
    }
} 