package dev.sakura.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.sakura.core.data.OrdersRepository
import dev.sakura.models.ItemsModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrdersRepositoryImpl @Inject constructor() : OrdersRepository {
    private val orderedItems = mutableListOf<ItemsModel>()
    private val _orderedItemsLiveData = MutableLiveData<List<ItemsModel>>(emptyList())
    override val orderedItemsLiveData: LiveData<List<ItemsModel>> = _orderedItemsLiveData

    init {
        updateLiveData()
    }

    override fun addOrderedItems(items: List<ItemsModel>) {
        orderedItems.addAll(0, items)
        updateLiveData()
    }

    private fun updateLiveData() {
        _orderedItemsLiveData.postValue(ArrayList(orderedItems))
    }
}
