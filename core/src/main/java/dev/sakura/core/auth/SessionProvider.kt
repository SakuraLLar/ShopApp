package dev.sakura.core.auth

interface SessionProvider {
    fun createLoginSession(userId: Long)
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): Long?
    fun logoutUser()
}
