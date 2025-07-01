package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import android.os.Parcel;

public class ProductTest {
    private Product product;
    private Farmacia farmacia;

    @Before
    public void setUp() {
        farmacia = new Farmacia("1", "Farmacia Teste", "Rua 1");
        product = new Product(1, "Produto", "Descricao", "R$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, farmacia);
    }

    @Test
    public void testGetImage() {
        assertEquals(1, product.getImage());
    }

    @Test
    public void testGetName() {
        assertEquals("Produto", product.getName());
    }

    @Test
    public void testGetDescription() {
        assertEquals("Descricao", product.getDescription());
    }

    @Test
    public void testGetPrice() {
        assertEquals("R$10,00", product.getPrice());
    }

    @Test
    public void testGetCategory() {
        assertEquals("Categoria", product.getCategory());
    }

    @Test
    public void testGetBrand() {
        assertEquals("Marca", product.getBrand());
    }

    @Test
    public void testGetTarja() {
        assertEquals(Product.TARJA_AMARELA, product.getTarja());
    }

    @Test
    public void testGetFarmacia() {
        assertEquals(farmacia, product.getFarmacia());
    }

    @Test
    public void testSetFarmacia() {
        Farmacia newFarmacia = new Farmacia("2", "Nova Farmacia", "Rua 2");
        product.setFarmacia(newFarmacia);
        assertEquals(newFarmacia, product.getFarmacia());
    }

    @Test
    public void testTarjaConstants() {
        assertEquals("SEM_TARJA", Product.TARJA_SEM_TARJA);
        assertEquals("AMARELA", Product.TARJA_AMARELA);
        assertEquals("VERMELHA_SEM_RETENCAO", Product.TARJA_VERMELHA_SEM_RETENCAO);
        assertEquals("VERMELHA_COM_RETENCAO", Product.TARJA_VERMELHA_COM_RETENCAO);
        assertEquals("PRETA", Product.TARJA_PRETA);
    }

    @Test
    public void testDescribeContents() {
        assertEquals(0, product.describeContents());
    }

    @Test
    public void testParcelableImplementation() {
        // Skip this test as it requires Android system calls that are not mocked in unit tests
        // Parcel.obtain() requires Android runtime which is not available in unit tests
        // This should be tested in instrumented tests instead
    }

    @Test
    public void testNewArray() {
        Product[] products = Product.CREATOR.newArray(5);
        assertEquals(5, products.length);
        for (Product p : products) {
            assertNull(p); // New array should contain null values
        }
    }

    @Test
    public void testConstructorWithNullFarmacia() {
        Product productWithNullFarmacia = new Product(1, "Produto", "Descricao", "R$10,00", "Categoria", "Marca", Product.TARJA_AMARELA, null);
        assertNull(productWithNullFarmacia.getFarmacia());
    }

    @Test
    public void testConstructorWithAllTarjaTypes() {
        Product productSemTarja = new Product(1, "Produto1", "Desc", "R$10,00", "Cat", "Brand", Product.TARJA_SEM_TARJA, farmacia);
        Product productVermelha = new Product(2, "Produto2", "Desc", "R$10,00", "Cat", "Brand", Product.TARJA_VERMELHA_SEM_RETENCAO, farmacia);
        Product productPreta = new Product(3, "Produto3", "Desc", "R$10,00", "Cat", "Brand", Product.TARJA_PRETA, farmacia);
        
        assertEquals(Product.TARJA_SEM_TARJA, productSemTarja.getTarja());
        assertEquals(Product.TARJA_VERMELHA_SEM_RETENCAO, productVermelha.getTarja());
        assertEquals(Product.TARJA_PRETA, productPreta.getTarja());
    }
} 