@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package dev.sakura.data.repository

import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.data.OrdersRepository
import dev.sakura.data.entities.OrderEntity
import dev.sakura.data.entities.OrderItemEntity
import dev.sakura.data.mappers.toModel
import dev.sakura.data.orders.OrdersDao
import dev.sakura.models.ItemsModel
import dev.sakura.models.OrderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrdersRepositoryImpl @Inject constructor(
    private val ordersDao: OrdersDao,
    private val sessionProvider: SessionProvider,
) : OrdersRepository {
    override fun getMyOrders(): Flow<List<OrderModel>> {
        return sessionProvider.getUserIdFlow().flatMapLatest { userId ->
            if (userId != null) {
                ordersDao.getOrdersForUser(userId).map { list ->
                    list.map { it.toModel() }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun createOrderFromCartItems(items: List<ItemsModel>): Result<Unit> =
        withContext(Dispatchers.IO) {
            val userId = sessionProvider.getCurrentUserId()

            if (userId == null) {
                return@withContext Result.failure(IllegalStateException("Авторизуйтесь для совершения покупок."))
            }

            if (items.isEmpty()) {
                return@withContext Result.failure(IllegalStateException("Корзина пуста"))
            }

            val totalPrice = items.sumOf { it.price * it.numberInCart }
            val orderEntity = OrderEntity(
                userId = userId,
                orderDate = Date(),
                status = "В обработке",
                totalPrice = totalPrice
            )
            val newOrderId = ordersDao.insertOrder(orderEntity)
            val orderItems = items.map {
                OrderItemEntity(
                    orderId = newOrderId,
                    productId = it.resourceId.toString(),
                    title = it.title,
                    price = it.price,
                    quantity = it.numberInCart,
                    imageResourceId = it.resourceId
                )
            }
            ordersDao.insertOrderItems(orderItems)
            return@withContext Result.success(Unit)
        }
}
