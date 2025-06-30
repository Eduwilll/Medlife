package medlife.com.br.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class OrderStatusTest {
    @Test
    public void testEnumValues() {
        assertEquals(OrderStatus.ORDER_CONFIRMED, OrderStatus.valueOf("ORDER_CONFIRMED"));
        assertEquals("ORDER_CONFIRMED", OrderStatus.ORDER_CONFIRMED.name());
    }
} 