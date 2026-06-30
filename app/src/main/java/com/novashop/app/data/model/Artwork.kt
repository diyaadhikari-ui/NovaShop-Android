package com.novashop.app.data.model

data class Artwork(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val artistBio: String = "",
    val categoryName: String = "",
    val basePrice: Double = 0.0,
    val imageUrl: String = "",
    val isLimitedEdition: Boolean = false,
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0L
)
