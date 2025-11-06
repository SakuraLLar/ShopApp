package dev.sakura.feature_auth.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.data.UserRepository
import dev.sakura.data.repository.CartRepositoryImpl
import dev.sakura.data.repository.FavouritesRepositoryImpl
import dev.sakura.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManagerImpl @Inject constructor(
    private val userRepository: UserRepository,
    private val cartRepository: CartRepositoryImpl,
    private val favouritesRepository: FavouritesRepositoryImpl,
    private val sessionProvider: SessionProvider,
) : AuthManager {
    private val _currentUser = MutableLiveData<UserModel?>()
    override val currentUser: LiveData<UserModel?> = _currentUser

    private val managerScope = CoroutineScope(Dispatchers.Main)

    init {
        sessionProvider.getUserIdFlow()
            .onEach { userId ->
                if (userId != null) {
                    loadCurrentUser(userId)
                } else {
                    _currentUser.postValue(null)
                }
            }
            .launchIn(managerScope)
    }

    override fun loadCurrentUser(userId: Long) {
        managerScope.launch {
            val result = withContext(Dispatchers.IO) { userRepository.getUserById(userId) }
            _currentUser.value = result.getOrNull()
        }
    }

    override suspend fun registerUser(user: UserModel): Result<UserModel?> {
        val registrationResult = userRepository.registerUserAndGetNewUser(user)
        registrationResult.getOrNull()?.let { newUser ->
            sessionProvider.createLoginSession(newUser.id)
            mergeGuestData(newUser.id)
        }
        return registrationResult
    }

    override suspend fun loginUser(emailOrPhone: String, password: String): Result<UserModel?> {
        val userResult = userRepository.getUserByEmailOrPhoneNumberForLogin(emailOrPhone)

        return if (userResult.isSuccess && userResult.getOrNull() != null) {
            val user = userResult.getOrNull()!!
            val passwordMatches = withContext(Dispatchers.Default) {
                BCrypt.checkpw(password, user.passwordHash)
            }

            if (passwordMatches) {
                sessionProvider.createLoginSession(user.id)
                mergeGuestData(user.id)
                Result.success(user)
            } else {
                Result.failure(Exception("Неверный логин или пароль"))
            }
        } else {
            userResult
        }
    }

    private suspend fun mergeGuestData(userId: Long) {
        withContext(Dispatchers.IO) {
            cartRepository.assignGuestCartToUser(userId)
            favouritesRepository.assignGuestFavouritesToUser(userId)
        }
    }
}
