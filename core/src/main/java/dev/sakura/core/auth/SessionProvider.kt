package dev.sakura.core.auth

import androidx.annotation.DrawableRes
import kotlinx.coroutines.flow.Flow

interface SessionProvider {
    fun createLoginSession(userId: Long)
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): Long?
    fun logoutUser()
    fun getUserIdFlow(): Flow<Long?>

    fun saveAvatarForCurrentUser(avatarUri: String)
    fun getAvatarForCurrentUser(): String?

    fun saveCoverForCurrentUser(@DrawableRes coverId: Int)
    fun getCoverForCurrentUser(): Int?
}
