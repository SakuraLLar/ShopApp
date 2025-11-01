package dev.sakura.feature_orders.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.data.OrdersRepository
import dev.sakura.models.ItemsModel
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository,
) : ViewModel() {
    val orderedItemsList: LiveData<List<ItemsModel>> = ordersRepository.orderedItemsLiveData
}
