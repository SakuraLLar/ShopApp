package dev.sakura.core.auth

import androidx.lifecycle.LiveData
import dev.sakura.models.UserModel

interface AuthManager {
    val currentUser: LiveData<UserModel?>
    fun loadCurrentUser(userId: Long)
    suspend fun registerUser(user: UserModel): Result<UserModel?>
    suspend fun loginUser(emailOrPhone: String, passwordHash: String): Result<UserModel?>
    suspend fun updateUserData(user: UserModel): Result<Unit>
}
