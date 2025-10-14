package dev.sakura.core.data

import dev.sakura.models.User

interface UserRepository {
    suspend fun registerUserAndGetNewUser(user: User): Result<User?>
    suspend fun getUserByEmailOrPhoneNumberForLogin(emailOrPhone: String): Result<User?>
    suspend fun getUserById(userId: Long): Result<User?>
}
