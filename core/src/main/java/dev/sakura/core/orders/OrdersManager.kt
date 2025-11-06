package dev.sakura.core.orders

import dev.sakura.core.data.OrdersRepository
import dev.sakura.models.ItemsModel
import dev.sakura.models.OrderModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface OrdersManager {
    fun getMyOrders(): Flow<List<OrderModel>>
    fun placeOrder(items: List<ItemsModel>, onResult: (Result<Unit>) -> Unit)
}

@Singleton
class OrdersManagerImpl @Inject constructor(
    private val ordersRepository: OrdersRepository,
) : OrdersManager {
    private val managerScope = CoroutineScope(Dispatchers.IO)

    override fun getMyOrders(): Flow<List<OrderModel>> {
        return ordersRepository.getMyOrders()
    }

    override fun placeOrder(
        items: List<ItemsModel>,
        onResult: (Result<Unit>) -> Unit,
    ) {
        managerScope.launch {
            val result = ordersRepository.createOrderFromCartItems(items)
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }
}
