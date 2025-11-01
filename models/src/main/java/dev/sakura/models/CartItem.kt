package dev.sakura.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val productId: String,
    val title: String,
    val price: Double,
    val imageResourcedId: Int?,
    var quantity: Int,
) {
    fun toItemsModel(): ItemsModel {
        return ItemsModel(
            resourceId = this.imageResourcedId ?: 0,
            title = this.title,
            price = this.price,
            numberInCart = this.quantity,
            description = "",
            size = ArrayList(),
            rating = 0.0,
            colorResourceNames = emptyList()
        )
    }
}
