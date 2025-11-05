package dev.sakura.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val imageResourcedId: Int?,
    var quantity: Int,
)
