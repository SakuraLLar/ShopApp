package dev.sakura.models

import java.util.Date

data class OrderModel(
    val id: Long,
    val orderDate: Date,
    val status: String,
    val totalPrice: Double,
    val items: List<ItemsModel>,
)
