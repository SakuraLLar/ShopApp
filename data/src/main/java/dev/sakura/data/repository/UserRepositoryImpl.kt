package dev.sakura.data.repository

import dev.sakura.core.data.UserRepository
import dev.sakura.data.mappers.toEntity
import dev.sakura.data.mappers.toModel
import dev.sakura.data.user.UserDao
import dev.sakura.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class UserAlreadyExistsException(message: String) : Exception(message)

@Singleton
class UserRepositoryImpl @Inject constructor(private val userDao: UserDao) : UserRepository {

    override suspend fun registerUserAndGetNewUser(user: UserModel): Result<UserModel?> {
        return withContext(Dispatchers.IO) {
            try {
                val userEntity = user.toEntity()

                if (userDao.doesUserExist(userEntity.email, userEntity.phoneNumber)) {
                    Result.failure(UserAlreadyExistsException("Пользователь с email ${userEntity.email} или телефоном ${userEntity.phoneNumber} уже существует."))
                } else {
                    val userId = userDao.insertUser(userEntity)
                    if (userId > 0) {
                        val newUserEntity = userDao.getUserById(userId)
                        Result.success(newUserEntity?.toModel())
                    } else {
                        Result.failure(Exception("Не удалось зарегистрировать пользователя. Код ошибки БД"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserByEmailOrPhoneNumberForLogin(emailOrPhone: String): Result<UserModel?> {
        return withContext(Dispatchers.IO) {
            try {
                val userEntity = userDao.getUserByEmailOrPhoneNumber(emailOrPhone)
                Result.success(userEntity?.toModel())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getUserById(userID: Long): Result<UserModel?> {
        return withContext(Dispatchers.IO) {
            try {
                val userEntity = userDao.getUserById(userID)
                Result.success(userEntity?.toModel())
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
