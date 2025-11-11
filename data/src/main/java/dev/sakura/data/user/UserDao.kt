package dev.sakura.data.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.sakura.data.entities.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhoneNumber(phoneNumber: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email OR phoneNumber = :phoneNumber LIMIT 1)")
    suspend fun doesUserExist(email: String, phoneNumber: String): Boolean

    @Query("SELECT * FROM users WHERE email = :emailOrPhone OR phoneNumber = :emailOrPhone LIMIT 1")
    suspend fun getUserByEmailOrPhoneNumber(emailOrPhone: String): UserEntity?

     @Update
     suspend fun updateUser(user: UserEntity)

    // Метод для удаления пользователя
    // @Delete
    // suspend fun deleteUser(user: UserEntity)
}

