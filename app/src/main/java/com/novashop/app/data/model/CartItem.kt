package com.novashop.app.data.model

data class CartItem(
    var id: String = "",
    var userId: String = "",
    var artworkId: String = "",
    var artworkTitle: String = "",
    var artworkImage: String = "",
    var price: Double = 0.0,
    var quantity: Int = 1,
    var totalPrice: Double = 0.0
)