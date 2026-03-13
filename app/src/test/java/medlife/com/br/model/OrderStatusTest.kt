package medlife.com.br.model

import org.junit.Assert.assertEquals
import org.junit.Test

class OrderStatusTest {
    @Test
    fun testEnumValues() {
        assertEquals(OrderStatus.ORDER_CONFIRMED, OrderStatus.valueOf("ORDER_CONFIRMED"))
        assertEquals("ORDER_CONFIRMED", OrderStatus.ORDER_CONFIRMED.name)
    }
}
