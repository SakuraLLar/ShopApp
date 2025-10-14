package dev.sakura.feature_auth.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.sakura.core.auth.AuthManager
import dev.sakura.data.repository.UserRepositoryImpl
import dev.sakura.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManagerImpl @Inject constructor(
    private val userRepository: UserRepositoryImpl,
) : AuthManager {
    private val _currentUser = MutableLiveData<User?>()
    override val currentUser: LiveData<User?> = _currentUser

    private val managerScope = CoroutineScope(Dispatchers.IO)

    override fun loadCurrentUser(userId: Long) {
        managerScope.launch {
            val result = userRepository.getUserById(userId)
            withContext(Dispatchers.Main) {
                _currentUser.value = result.getOrNull()
            }
        }
    }
}
