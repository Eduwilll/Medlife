package medlife.com.br.model

data class CartItem(
    var product: Product? = null,
    var quantity: Int = 0
) {
    val price: Double
        get() = (product?.price?.replace("R$", "")?.replace(",", ".")?.trim()?.toDoubleOrNull() ?: 0.0) * quantity
}
