package dev.sakura.models

data class CartItemModel(
    val productId: String,
    val title: String,
    val price: Double,
    val imageResourceId: Int?,
    val quantity: Int,
    val isSelected: Boolean = true,
)
