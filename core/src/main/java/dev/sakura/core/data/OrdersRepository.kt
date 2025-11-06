package dev.sakura.core.data

import dev.sakura.models.ItemsModel
import dev.sakura.models.OrderModel
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {
    fun getMyOrders(): Flow<List<OrderModel>>

    suspend fun createOrderFromCartItems(items: List<ItemsModel>): Result<Unit>
}
