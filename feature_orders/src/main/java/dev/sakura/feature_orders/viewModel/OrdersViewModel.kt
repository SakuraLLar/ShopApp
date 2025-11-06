package dev.sakura.feature_orders.viewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.data.OrdersRepository
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository,
) : ViewModel() {
    val orderedItemsList: Flow<List<ItemsModel>> = ordersRepository.getMyOrders().map { orders ->
        orders.flatMap { order ->
            order.items.map { item ->
                item
            }
        }
    }
}
