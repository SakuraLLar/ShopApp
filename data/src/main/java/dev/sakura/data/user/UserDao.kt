package dev.sakura.shopapp.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByPhoneNumber(phoneNumber: String): User?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email OR phoneNumber = :phoneNumber LIMIT 1)")
    suspend fun doesUserExist(email: String, phoneNumber: String): Boolean

    @Query("SELECT * FROM users WHERE email = :emailOrPhone OR phoneNumber = :emailOrPhone LIMIT 1")
    suspend fun getUserByEmailOrPhoneNumber(emailOrPhone: String): User?


    // Метод для обновления пользователя
    // @Update
    // suspend fun updateUser(user: User)
    //
    // Метод для удаления пользователя
    // @Delete
    // suspend fun deleteUser(user: User)
}

