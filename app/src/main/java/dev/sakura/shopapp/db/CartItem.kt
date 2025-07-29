package dev.sakura.shopapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val imageResourcedId: Int?,
    var quantity: Int,
)
