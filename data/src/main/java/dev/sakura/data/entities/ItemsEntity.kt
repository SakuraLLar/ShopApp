package dev.sakura.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ItemsEntity(
    @PrimaryKey
    var resourceId: Int = 0,
    var title: String = "",
    val description: String = "",
    var size: List<String> = emptyList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    val colorResourceNames: List<String> = emptyList(),
)
