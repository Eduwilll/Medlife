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

    // Add more tests for other fields as needed
} 