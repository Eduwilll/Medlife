package medlife.com.br.helper

import medlife.com.br.model.CartItem
import medlife.com.br.model.Order
import medlife.com.br.model.OrderStatus
import medlife.com.br.model.Product

object CartManager {

    private val cartItems: MutableList<CartItem> = mutableListOf()

    fun addProduct(product: Product, quantity: Int) {
        val existing = cartItems.find { it.product?.name == product.name }
        if (existing != null) {
            existing.quantity += quantity
        } else {
            cartItems.add(CartItem(product, quantity))
        }
    }

    fun removeProduct(cartItem: CartItem) {
        cartItems.remove(cartItem)
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity > 0) {
            cartItem.quantity = newQuantity
        } else {
            removeProduct(cartItem)
        }
    }

    fun getTotalPrice(): Double {
        return cartItems.sumOf { item ->
            val priceString = item.product?.price
                ?.replace("R$", "")
                ?.replace(",", ".")
                ?.trim() ?: return@sumOf 0.0
            try {
                priceString.toDouble() * item.quantity
            } catch (e: NumberFormatException) {
                0.0
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getCartItems(): List<CartItem> = cartItems

    fun createOrderFromCart(): Order? {
        val userId = UsuarioFirebase.idUsuario ?: return null
        if (cartItems.isEmpty()) return null

        val orderItems: List<Map<String, Any>> = cartItems.mapNotNull { item ->
            val product = item.product ?: return@mapNotNull null
            val price = product.price
                ?.replace("R$", "")
                ?.replace(",", ".")
                ?.trim()
                ?.toDoubleOrNull() ?: 0.0
            mapOf(
                "productName" to (product.name ?: ""),
                "quantity" to item.quantity,
                "price" to (product.price ?: ""),
                "totalItemPrice" to (price * item.quantity),
                "category" to (product.category ?: ""),
                "brand" to (product.brand ?: ""),
                "tarja" to (product.tarja ?: ""),
                "description" to (product.description ?: ""),
                "farmacia" to (product.farmacia ?: "")
            )
        }

        return Order().apply {
            this.userId = userId
            this.items = orderItems
            this.totalPrice = getTotalPrice()
            this.status = OrderStatus.ORDER_CONFIRMED.toString()
            this.subtotal = getTotalPrice()
            this.deliveryOption = "immediate"
            this.deliveryFee = 7.00
            this.discountAmount = 0.0
            this.paymentMethod = "Pendente"
            this.paymentStatus = "pending"
            this.isRequiresPrescription = false
        }
    }
}
