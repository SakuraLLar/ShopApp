package dev.sakura.data.repository

import dev.sakura.models.User
import dev.sakura.core.data.UserRepository
import dev.sakura.data.user.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class UserAlreadyExistsException(message: String) : Exception(message)

@Singleton
class UserRepositoryImpl @Inject constructor(private val userDao: UserDao) : UserRepository{

    override suspend fun registerUserAndGetNewUser(user: User): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                if (userDao.doesUserExist(user.email, user.phoneNumber)) {
                    Result.failure(UserAlreadyExistsException("Пользователь с email ${user.email} или телефоном ${user.phoneNumber} уже существует."))
                } else {
                    val userId = userDao.insertUser(user)
                    if (userId > 0) {
                        val newUser = userDao.getUserById(userId)
                        if (newUser != null) {
                            Result.success(newUser)
                        } else {
                            Result.failure(Exception("Не удалось получить данные нового пользователя после регистрации."))
                        }
                    } else {
                        Result.failure(Exception("Не удалось зарегистрировать пользователя. Код ошибки БД"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserByEmailOrPhoneNumberForLogin(emailOrPhone: String): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByEmailOrPhoneNumber(emailOrPhone)
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserById(userID: Long): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(userDao.getUserById(userID))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
