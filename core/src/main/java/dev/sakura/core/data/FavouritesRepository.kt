package dev.sakura.core.data

import androidx.lifecycle.LiveData
import dev.sakura.models.ItemsModel

interface FavouritesRepository {
    val favouritesLiveData: LiveData<List<ItemsModel>>
    val isFavouritesStatusLiveData: LiveData<Map<Int, Boolean>>

    fun addItem(item: ItemsModel)
    fun removeItem(item: ItemsModel)
    fun toggleFavouritesStatus(item: ItemsModel)
    fun getFavourites(): List<ItemsModel>
    fun isItemFavourite(itemId: Int): Boolean
}
