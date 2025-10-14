package dev.sakura.core.auth

import androidx.lifecycle.LiveData
import dev.sakura.models.User

interface AuthManager {
    val currentUser: LiveData<User?>
    fun loadCurrentUser(userId: Long)
}
