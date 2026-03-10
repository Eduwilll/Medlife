package medlife.com.br.model

import com.google.firebase.Timestamp

data class Order(
    var orderId: String? = null,
    var userId: String? = null,
    var items: List<Map<String, Any>>? = null,
    var totalPrice: Double = 0.0,
    var status: String? = null,
    var createdAt: Timestamp? = null,
    
    // Delivery Information
    var deliveryOption: String? = null, // "immediate", "pickup", "scheduled"
    var deliveryFee: Double = 0.0,
    var deliveryAddress: Map<String, Any>? = null,
    
    // Payment Information
    var paymentMethod: String? = null,
    var paymentStatus: String? = null, // "pending", "paid", "failed"
    
    // Summary Breakdown
    var subtotal: Double = 0.0,
    var discountAmount: Double = 0.0,
    var couponCode: String? = null,
    
    // Store Information
    var storeName: String? = null,
    var storeLocation: String? = null,
    
    // Additional Information
    var notes: String? = null,
    var estimatedDeliveryTime: Timestamp? = null,
    var isRequiresPrescription: Boolean = false,
    
    // Prescription Information
    var prescriptionIds: List<String>? = null,
    var prescriptionStatus: String? = null, // "pending", "approved", "rejected"
    var prescriptionUploadDate: Timestamp? = null
)
