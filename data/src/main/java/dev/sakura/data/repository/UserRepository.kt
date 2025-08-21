package dev.sakura.shopapp.data

import dev.sakura.shopapp.db.user.User
import dev.sakura.shopapp.db.user.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

//    suspend fun registerUser(user: User): Result<Long> {
//        return withContext(Dispatchers.IO) {
//            try {
//                if (userDao.doesUserExist(user.email, user.phoneNumber)) {
//                    Result.failure(UserAlreadyExistsException("Полльзователь с email ${user.email} или телефоном ${user.phoneNumber} уже существует."))
//                } else {
//                    val userId = userDao.insertUser(user)
//
//                    if (userId > 0) {
//                        Result.success(userId)
//                    } else {
//                        Result.failure(Exception("Не удалось зарегистрировать пользователя. Код ошибки БД"))
//                    }
//                }
//            } catch (e: Exception) {
//                Result.failure(e)
//            }
//        }
//    }

    suspend fun registerUserAndGetNewUser(user: User): Result<User?> { // <--- НОВЫЙ МЕТОД
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

    suspend fun getUserByEmailOrPhoneNumberForLogin(emailOrPhone: String): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.getUserByEmailOrPhoneNumber(emailOrPhone)
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getUserById(userID: Long): Result<User?> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(userDao.getUserById(userID))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    class UserAlreadyExistsException(message: String) : Exception(message)
}
