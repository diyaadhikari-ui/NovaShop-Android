package com.novashop.app.data.model

class User (
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "customer",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val createdAt: Long = 0L
)
