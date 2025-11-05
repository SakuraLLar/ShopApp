package dev.sakura.core.data

import dev.sakura.models.UserModel


interface UserRepository {
    suspend fun registerUserAndGetNewUser(user: UserModel): Result<UserModel?>
    suspend fun getUserByEmailOrPhoneNumberForLogin(emailOrPhone: String): Result<UserModel?>
    suspend fun getUserById(userId: Long): Result<UserModel?>
}
