package com.novashop.app.data.model

data class Order(
    val id: String = "",
    val orderNumber: String = "",
    val userId: String = "",
    val status: String = "pending",
    val paymentStatus: String = "unpaid",
    val paymentMethod: String = "",
    val subtotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val shippingFullName: String = "",
    val shippingPhone: String = "",
    val shippingAddress: String = "",
    val shippingCity: String = "",
    val shippingProvince: String = "",
    val items: List<OrderItem> = emptyList(),
    val createdAt: Long = 0L
)

data class OrderItem(
    val artworkId: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val totalPrice: Double = 0.0
)