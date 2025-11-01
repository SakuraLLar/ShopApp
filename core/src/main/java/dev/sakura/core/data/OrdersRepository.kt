package dev.sakura.core.data

import androidx.lifecycle.LiveData
import dev.sakura.models.ItemsModel

interface OrdersRepository {
    val orderedItemsLiveData: LiveData<List<ItemsModel>>
    fun addOrderedItems(item: List<ItemsModel>)
}
