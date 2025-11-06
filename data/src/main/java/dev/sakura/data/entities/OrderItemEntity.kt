package dev.sakura.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "order_items",
    primaryKeys = ["orderId", "productId"],
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderItemEntity(
    val orderId: Long,
    val productId: String,
    val title: String,
    val price: Double,
    val quantity: Int,
    val imageResourceId: Int?,
)
