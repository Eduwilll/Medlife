package medlife.com.br.model

import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class OrderTest {
    private lateinit var order: Order

    @Before
    fun setUp() {
        order = Order()
    }

    @Test fun testSetAndGetOrderId() {
        order.orderId = "123"
        assertEquals("123", order.orderId)
    }

    @Test fun testSetAndGetUserId() {
        order.userId = "user1"
        assertEquals("user1", order.userId)
    }

    @Test fun testSetAndGetItems() {
        val items: List<Map<String, Any>> = listOf(mapOf("productName" to "TestProduct"))
        order.items = items
        assertEquals(items, order.items)
    }

    @Test fun testSetAndGetTotalPrice() {
        order.totalPrice = 99.99
        assertEquals(99.99, order.totalPrice, 0.001)
    }

    @Test fun testSetAndGetStatus() {
        order.status = "ORDER_CONFIRMED"
        assertEquals("ORDER_CONFIRMED", order.status)
    }

    @Test fun testSetAndGetCreatedAt() {
        val now = Timestamp.now()
        order.createdAt = now
        assertEquals(now, order.createdAt)
    }

    @Test fun testSetAndGetDeliveryOption() {
        order.deliveryOption = "immediate"
        assertEquals("immediate", order.deliveryOption)
    }

    @Test fun testSetAndGetDeliveryFee() {
        order.deliveryFee = 5.50
        assertEquals(5.50, order.deliveryFee, 0.001)
    }

    @Test fun testSetAndGetDeliveryAddress() {
        val address: Map<String, Any> = mapOf("street" to "Rua Teste", "city" to "São Paulo")
        order.deliveryAddress = address
        assertEquals(address, order.deliveryAddress)
    }

    @Test fun testSetAndGetPaymentMethod() {
        order.paymentMethod = "credit_card"
        assertEquals("credit_card", order.paymentMethod)
    }

    @Test fun testSetAndGetPaymentStatus() {
        order.paymentStatus = "paid"
        assertEquals("paid", order.paymentStatus)
    }

    @Test fun testSetAndGetSubtotal() {
        order.subtotal = 85.50
        assertEquals(85.50, order.subtotal, 0.001)
    }

    @Test fun testSetAndGetDiscountAmount() {
        order.discountAmount = 10.00
        assertEquals(10.00, order.discountAmount, 0.001)
    }

    @Test fun testSetAndGetCouponCode() {
        order.couponCode = "SAVE10"
        assertEquals("SAVE10", order.couponCode)
    }

    @Test fun testSetAndGetStoreName() {
        order.storeName = "Farmacia Central"
        assertEquals("Farmacia Central", order.storeName)
    }

    @Test fun testSetAndGetStoreLocation() {
        order.storeLocation = "Shopping Center"
        assertEquals("Shopping Center", order.storeLocation)
    }

    @Test fun testSetAndGetNotes() {
        order.notes = "Leave at front door"
        assertEquals("Leave at front door", order.notes)
    }

    @Test fun testSetAndGetEstimatedDeliveryTime() {
        val deliveryTime = Timestamp.now()
        order.estimatedDeliveryTime = deliveryTime
        assertEquals(deliveryTime, order.estimatedDeliveryTime)
    }

    @Test fun testSetAndGetRequiresPrescription() {
        order.requiresPrescription = true
        assertTrue(order.requiresPrescription)
        order.requiresPrescription = false
        assertFalse(order.requiresPrescription)
    }

    @Test fun testSetAndGetPrescriptionIds() {
        val ids = listOf("presc1", "presc2")
        order.prescriptionIds = ids
        assertEquals(ids, order.prescriptionIds)
    }

    @Test fun testSetAndGetPrescriptionStatus() {
        order.prescriptionStatus = "approved"
        assertEquals("approved", order.prescriptionStatus)
    }

    @Test fun testSetAndGetPrescriptionUploadDate() {
        val uploadDate = Timestamp.now()
        order.prescriptionUploadDate = uploadDate
        assertEquals(uploadDate, order.prescriptionUploadDate)
    }

    @Test fun testDefaultConstructor() {
        val newOrder = Order()
        assertNotNull(newOrder)
        assertNull(newOrder.orderId)
        assertNull(newOrder.userId)
        assertNull(newOrder.items)
        assertEquals(0.0, newOrder.totalPrice, 0.001)
        assertNull(newOrder.status)
        assertNull(newOrder.createdAt)
    }
}
