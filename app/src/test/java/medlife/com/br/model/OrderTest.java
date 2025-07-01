package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.google.firebase.Timestamp;
import java.util.*;

public class OrderTest {
    private Order order;

    @Before
    public void setUp() {
        order = new Order();
    }

    @Test
    public void testSetAndGetOrderId() {
        order.setOrderId("123");
        assertEquals("123", order.getOrderId());
    }

    @Test
    public void testSetAndGetUserId() {
        order.setUserId("user1");
        assertEquals("user1", order.getUserId());
    }

    @Test
    public void testSetAndGetItems() {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productName", "TestProduct");
        items.add(item);
        order.setItems(items);
        assertEquals(items, order.getItems());
    }

    @Test
    public void testSetAndGetTotalPrice() {
        order.setTotalPrice(99.99);
        assertEquals(99.99, order.getTotalPrice(), 0.001);
    }

    @Test
    public void testSetAndGetStatus() {
        order.setStatus("ORDER_CONFIRMED");
        assertEquals("ORDER_CONFIRMED", order.getStatus());
    }

    @Test
    public void testSetAndGetCreatedAt() {
        Timestamp now = Timestamp.now();
        order.setCreatedAt(now);
        assertEquals(now, order.getCreatedAt());
    }

    @Test
    public void testSetAndGetDeliveryOption() {
        order.setDeliveryOption("immediate");
        assertEquals("immediate", order.getDeliveryOption());
    }

    @Test
    public void testSetAndGetDeliveryFee() {
        order.setDeliveryFee(5.50);
        assertEquals(5.50, order.getDeliveryFee(), 0.001);
    }

    @Test
    public void testSetAndGetDeliveryAddress() {
        Map<String, Object> address = new HashMap<>();
        address.put("street", "Rua Teste");
        address.put("city", "São Paulo");
        order.setDeliveryAddress(address);
        assertEquals(address, order.getDeliveryAddress());
    }

    @Test
    public void testSetAndGetPaymentMethod() {
        order.setPaymentMethod("credit_card");
        assertEquals("credit_card", order.getPaymentMethod());
    }

    @Test
    public void testSetAndGetPaymentStatus() {
        order.setPaymentStatus("paid");
        assertEquals("paid", order.getPaymentStatus());
    }

    @Test
    public void testSetAndGetSubtotal() {
        order.setSubtotal(85.50);
        assertEquals(85.50, order.getSubtotal(), 0.001);
    }

    @Test
    public void testSetAndGetDiscountAmount() {
        order.setDiscountAmount(10.00);
        assertEquals(10.00, order.getDiscountAmount(), 0.001);
    }

    @Test
    public void testSetAndGetCouponCode() {
        order.setCouponCode("SAVE10");
        assertEquals("SAVE10", order.getCouponCode());
    }

    @Test
    public void testSetAndGetStoreName() {
        order.setStoreName("Farmacia Central");
        assertEquals("Farmacia Central", order.getStoreName());
    }

    @Test
    public void testSetAndGetStoreLocation() {
        order.setStoreLocation("Shopping Center");
        assertEquals("Shopping Center", order.getStoreLocation());
    }

    @Test
    public void testSetAndGetNotes() {
        order.setNotes("Leave at front door");
        assertEquals("Leave at front door", order.getNotes());
    }

    @Test
    public void testSetAndGetEstimatedDeliveryTime() {
        Timestamp deliveryTime = Timestamp.now();
        order.setEstimatedDeliveryTime(deliveryTime);
        assertEquals(deliveryTime, order.getEstimatedDeliveryTime());
    }

    @Test
    public void testSetAndGetRequiresPrescription() {
        order.setRequiresPrescription(true);
        assertTrue(order.isRequiresPrescription());
        
        order.setRequiresPrescription(false);
        assertFalse(order.isRequiresPrescription());
    }

    @Test
    public void testSetAndGetPrescriptionIds() {
        List<String> prescriptionIds = Arrays.asList("presc1", "presc2");
        order.setPrescriptionIds(prescriptionIds);
        assertEquals(prescriptionIds, order.getPrescriptionIds());
    }

    @Test
    public void testSetAndGetPrescriptionStatus() {
        order.setPrescriptionStatus("approved");
        assertEquals("approved", order.getPrescriptionStatus());
    }

    @Test
    public void testSetAndGetPrescriptionUploadDate() {
        Timestamp uploadDate = Timestamp.now();
        order.setPrescriptionUploadDate(uploadDate);
        assertEquals(uploadDate, order.getPrescriptionUploadDate());
    }

    @Test
    public void testDefaultConstructor() {
        Order newOrder = new Order();
        assertNotNull(newOrder);
        assertNull(newOrder.getOrderId());
        assertNull(newOrder.getUserId());
        assertNull(newOrder.getItems());
        assertEquals(0.0, newOrder.getTotalPrice(), 0.001);
        assertNull(newOrder.getStatus());
        assertNull(newOrder.getCreatedAt());
    }
} 