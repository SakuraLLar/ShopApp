package dev.sakura.core.auth

import kotlinx.coroutines.flow.Flow

interface SessionProvider {
    fun createLoginSession(userId: Long)
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): Long?
    fun logoutUser()
    fun getUserIdFlow(): Flow<Long?>
}
