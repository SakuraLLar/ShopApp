package dev.sakura.core.auth

import androidx.lifecycle.LiveData
import dev.sakura.models.UserModel

interface AuthManager {
    val currentUser: LiveData<UserModel?>
    fun loadCurrentUser(userId: Long)
}
