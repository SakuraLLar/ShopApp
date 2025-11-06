package dev.sakura.data.orders

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.sakura.data.entities.OrderEntity
import dev.sakura.data.entities.OrderItemEntity
import dev.sakura.data.entities.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Transaction
    @Query("SELeCT * FROM orders WHERE userId = :userId ORDER BY orderDate DESC")
    fun getOrdersForUser(userId: Long): Flow<List<OrderWithItems>>
}
