package dev.sakura.core.orders

import dev.sakura.core.data.OrdersRepository
import dev.sakura.models.ItemsModel
import javax.inject.Inject
import javax.inject.Singleton

interface OrdersManager {
    fun placeOrder(items: List<ItemsModel>)
}

@Singleton
class OrdersManagerImpl @Inject constructor(
    private val ordersRepository: OrdersRepository,
) : OrdersManager {
    override fun placeOrder(items: List<ItemsModel>) {
        ordersRepository.addOrderedItems(items)
    }
}

