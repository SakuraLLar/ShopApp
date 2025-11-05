package dev.sakura.models

data class UserModel(
    val id: Long = 0,
    val firstName: String,
    val lastName: String?,
    val email: String,
    val phoneNumber: String,
    val passwordHash: String,
    val gender: String?,
)
