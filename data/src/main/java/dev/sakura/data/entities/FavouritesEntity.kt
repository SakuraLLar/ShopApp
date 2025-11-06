package dev.sakura.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favourites",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId", "productId"], unique = true)]
)
data class FavouritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long?,
    val productId: String,
)
