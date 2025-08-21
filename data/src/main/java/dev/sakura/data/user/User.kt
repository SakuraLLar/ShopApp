package dev.sakura.shopapp.db.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true), Index(
        value = ["phoneNumber"],
        unique = true
    )]
)

data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String?,
    val email: String,
    val phoneNumber: String,
    val passwordHash: String,
    val gender: String?,
)
