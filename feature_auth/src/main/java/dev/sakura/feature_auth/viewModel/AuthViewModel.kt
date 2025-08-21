package dev.sakura.shopapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dev.sakura.shopapp.data.UserRepository
import dev.sakura.shopapp.db.AppDatabase
import dev.sakura.shopapp.db.user.User
import dev.sakura.shopapp.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User? = null, val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository

    private val _authState = MutableLiveData<AuthState>(AuthState.Idle)
    val authState: LiveData<AuthState> = _authState

    private val _navigationToMain = MutableLiveData<Event<User>>()
    val navigationToMain: LiveData<Event<User>> = _navigationToMain

    private val _registrationSuccessAction = MutableLiveData<Event<User>>()
    val registrationSuccessAction: LiveData<Event<User>> = _registrationSuccessAction

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        userRepository = UserRepository(userDao)

    }

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

                val userToRegister = User(
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
                    } else {
                        _authState.value =
                            AuthState.Error("Ошибка регистрации: не удалось получить данные пользователя.")
                    }
                } else {
                    val exception = registrationResult.exceptionOrNull()
                    val errorMessage = when (exception) {
                        is UserRepository.UserAlreadyExistsException -> "Пользователь с такими данными уже существует."
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

    fun loadCurrentUser(userId: Long) {
        viewModelScope.launch {
            val result = userRepository.getUserById(userId)
            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _authState.value =
                    AuthState.Idle
            } else {
                _currentUser.value = null
                _authState.value =
                    AuthState.Error("Не удалось загрузить данные пользователя.")
            }
        }
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
