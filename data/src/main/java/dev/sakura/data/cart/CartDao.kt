package dev.sakura.data.cart

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.sakura.data.entities.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(cartItem: CartItemEntity)

    @Update
    suspend fun updateItem(cartItem: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId AND userId = :userId")
    suspend fun deleteItemById(productId: String, userId: Long)

    @Query("DELETE FROM cart_items WHERE productId = :productId AND userId IS NULL")
    suspend fun deleteGuestItemById(productId: String)

    @Query("SELECT * FROM cart_items WHERE userId = :userId")
    fun getAllItems(userId: Long): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE userId IS NULL")
    fun getGuestAllItems(): Flow<List<CartItemEntity>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND userId = :userId")
    suspend fun getItemById(productId: String, userId: Long): CartItemEntity?

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND userId IS NULL")
    suspend fun getGuestItemById(productId: String): CartItemEntity?

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: Long)

    @Query("DELETE FROM cart_items WHERE userId IS NULL")
    suspend fun clearGuestCart()

    @Query("SELECT SUM(price * quantity) FROM cart_items WHERE userId = :userId")
    fun getTotalPrice(userId: Long): Flow<Double?>

    @Query("SELECT SUM(price * quantity) FROM cart_items WHERE userId IS NULL")
    fun getGuestTotalPrice(): Flow<Double?>

    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    fun getTotalItemCount(userId: Long): Flow<Int?>

    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId IS NULL")
    fun getGuestTotalItemCount(): Flow<Int?>

    @Query("UPDATE cart_items SET userId = :userId WHERE userId IS NULL")
    suspend fun assignGuestCartToUser(userId: Long)
}
