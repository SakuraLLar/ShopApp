package dev.sakura.feature_auth.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.data.UserRepository
import dev.sakura.data.repository.UserRepositoryImpl
import dev.sakura.data.entities.UserEntity
import dev.sakura.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManagerImpl @Inject constructor(
    private val userRepository: UserRepository,
) : AuthManager {
    private val _currentUser = MutableLiveData<UserModel?>()
    override val currentUser: LiveData<UserModel?> = _currentUser

    private val managerScope = CoroutineScope(Dispatchers.IO)

    override fun loadCurrentUser(userId: Long) {
        managerScope.launch {
            val result = userRepository.getUserById(userId)
            withContext(Dispatchers.Main) {
                _currentUser.value = result.getOrNull()
            }
        }
    }

//    override suspend fun registerUser(user: UserModel): Result<UserModel?> {
//        return userRepository.registerUserAndGetNewUser(user)
//    }
//
//    override suspend fun loginUser(emailOrPhone: String): Result<UserModel?> {
//        return userRepository.getUserByEmailOrPhoneNumberForLogin(emailOrPhone)
//    }
}
