package dev.sakura.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.sakura.core.data.FavouritesRepository
import dev.sakura.models.ItemsModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesRepositoryImpl @Inject constructor() : FavouritesRepository {
    private val favourites = mutableListOf<ItemsModel>()
    private val _favouritesLiveData = MutableLiveData<List<ItemsModel>>(emptyList())
    override val favouritesLiveData: LiveData<List<ItemsModel>> = _favouritesLiveData

    private val _isFavouriteStatusLiveData = MutableLiveData<Map<Int, Boolean>>(emptyMap())
    override val isFavouritesStatusLiveData: LiveData<Map<Int, Boolean>> =
        _isFavouriteStatusLiveData

    init {
        updateLiveData()
    }

    override fun addItem(item: ItemsModel) {
        if (favourites.none { it.resourceId == item.resourceId }) {
            favourites.add(item)
            updateLiveData()
        }
    }

    override fun removeItem(item: ItemsModel) {
        if (favourites.removeAll { it.resourceId == item.resourceId }) {
            updateLiveData()
        }
    }

    override fun toggleFavouritesStatus(item: ItemsModel) {
        if (isItemFavourite(item.resourceId)) {
            removeItem(item)
        } else {
            addItem(item)
        }
    }

    override fun isItemFavourite(itemId: Int): Boolean {
        return favourites.any { it.resourceId == itemId }
    }

    fun toggleFavouriteStatus(item: ItemsModel) {
        if (isItemFavourite(item.resourceId)) {
            removeItem(item)
        } else {
            addItem(item)
        }
    }

    override fun getFavourites(): List<ItemsModel> {
        return ArrayList(favourites)
    }

    private fun updateLiveData() {
        _favouritesLiveData.postValue(ArrayList(favourites))
        val statusMap = favourites.associate { it.resourceId to true }
        _isFavouriteStatusLiveData.postValue(statusMap)
    }
}
