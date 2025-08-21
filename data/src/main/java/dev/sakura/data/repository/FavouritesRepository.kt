package dev.sakura.shopapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.sakura.shopapp.model.ItemsModel

object FavouritesRepository {
    private val favourites = mutableListOf<ItemsModel>()
    private val _favouritesLiveData = MutableLiveData<List<ItemsModel>>(emptyList())
    val favouritesLiveData: LiveData<List<ItemsModel>> = _favouritesLiveData

    private val _isFavouriteStatusLiveData = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    val isFavouritesStatusLiveData: LiveData<Map<Int, Boolean>> = _isFavouriteStatusLiveData

    init {
        updateLiveData()
    }

    fun addItem(item: ItemsModel) {
        if (favourites.none { it.resourceId == item.resourceId }) {
            favourites.add(item)
            updateLiveData()
        }
    }

    fun removeItem(item: ItemsModel) {
        if (favourites.removeAll { it.resourceId == item.resourceId }) {
            updateLiveData()
        }
    }

    fun removeItemById(itemId: Int) {
        if (favourites.removeAll { it.resourceId == itemId }) {
            updateLiveData()
        }
    }

    fun isItemFavourite(itemId: Int): Boolean {
        return favourites.any { it.resourceId == itemId }
    }

    fun toggleFavouriteStatus(item: ItemsModel) {
        if (isItemFavourite(item.resourceId)) {
            removeItem(item)
        } else {
            addItem(item)
        }
    }

    fun getFavourites(): List<ItemsModel> {
        return ArrayList(favourites)
    }

    private fun updateLiveData() {
        _favouritesLiveData.postValue(ArrayList(favourites))
        val statusMap = favourites.associate { it.resourceId to true }
        _isFavouriteStatusLiveData.postValue(statusMap)
    }
}
