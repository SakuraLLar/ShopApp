package dev.sakura.core.favourites

import androidx.lifecycle.LiveData
import dev.sakura.models.ItemsModel

interface FavouritesManager {
    fun addItemToFavourites(item: ItemsModel)
    fun removeItemFromFavourites(item: ItemsModel)
    fun toggleFavouriteStatus(item: ItemsModel)
    val favouriteStatusMap: LiveData<Map<Int, Boolean>>
    fun isFavourite(itemId: Int): Boolean
//    fun getFavouritesCount(): Flow<Int>
}
