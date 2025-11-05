package dev.sakura.feature_auth.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.data.UserRepository
import dev.sakura.core.util.Event
import dev.sakura.data.entities.UserEntity
import dev.sakura.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: UserModel? = null, val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository,
    private val authManager: AuthManager
) : AndroidViewModel(application) {

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private val _navigationToMain = MutableLiveData<Event<UserModel>>()
    val navigationToMain: LiveData<Event<UserModel>> = _navigationToMain

    private val _registrationSuccessAction = MutableLiveData<Event<UserModel>>()
    val registrationSuccessAction: LiveData<Event<UserModel>> = _registrationSuccessAction

    val currentUser: LiveData<UserModel?> = authManager.currentUser

    fun registerUser(
        firstName: String,
        lastName: String?,
        email: String,
        phoneNumber: String,
        password: String,
        gender: String?,
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val passwordHash = hashPasswordWithBcrypt(password)

                val userToRegister = UserModel(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber,
                    passwordHash = passwordHash,
                    gender = gender
                )

                val registrationResult = userRepository.registerUserAndGetNewUser(userToRegister)
                if (registrationResult.isSuccess) {
                    val registeredUser = registrationResult.getOrNull()
                    if (registeredUser != null) {
                        _authState.value = AuthState.Success(
                            user = registeredUser,
                            message = "Регистрация успешна!"
                        )
                        _registrationSuccessAction.value = Event(registeredUser)
                        authManager.loadCurrentUser(registeredUser.id)
                    } else {
                        _authState.value =
                            AuthState.Error("Ошибка регистрации: не удалось получить данные пользователя.")
                    }
                } else {
                    val exception = registrationResult.exceptionOrNull()
                    val errorMessage = when (exception) {
                        is UserRepository -> "Пользователь с такими данными уже существует."
                        else -> "Ошибка регистрации: ${exception?.message ?: "Неизвестная ошибка."}"
                    }
                    _authState.value = AuthState.Error(errorMessage)
                }

            } catch (e: Exception) {
                _authState.value = AuthState.Error("Произошла ошибка ${e.message}")
            }
        }
    }

    fun loginUser(emailOrPhone: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userResult =
                    userRepository.getUserByEmailOrPhoneNumberForLogin(emailOrPhone)
                if (userResult.isSuccess) {
                    val user = userResult.getOrNull()
                    if (user != null) {
                        if (checkPasswordWithBCrypt(password, user.passwordHash)) {
                            authManager.loadCurrentUser(user.id)
                            _navigationToMain.value = Event(user)
                        } else {
                            _authState.value = AuthState.Error("Неверный логин или пароль.")
                        }
                    } else {
                        _authState.value = AuthState.Error("Пользователь не найден.")
                    }
                } else {
                    val exception = userResult.exceptionOrNull()
                    _authState.value =
                        AuthState.Error("Ошибка входа: ${exception?.message ?: "Неверный логин или пароль."}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Произошла ошибка входа ${e.message}")
            }
        }
    }

    fun loadCurrentUserFromSession(userId: Long) {
        authManager.loadCurrentUser(userId)
    }

    private suspend fun hashPasswordWithBcrypt(password: String): String {
        return withContext(Dispatchers.Default) {
            BCrypt.hashpw(password, BCrypt.gensalt())
        }
    }

    private suspend fun checkPasswordWithBCrypt(
        plainPassword: String,
        hashedPasswordFromDB: String,
    ): Boolean {
        return withContext(Dispatchers.Default) {
            try {
                BCrypt.checkpw(plainPassword, hashedPasswordFromDB)
            } catch (e: Exception) {
                false
            }
        }
    }

    fun onLoginNavigationComplete() {
        _authState.value = AuthState.Idle
    }

    fun onRegistrationNavigationComplete() {
        _authState.value = AuthState.Idle
    }
}
