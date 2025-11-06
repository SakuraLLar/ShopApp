package dev.sakura.core.favourites

import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow

interface FavouritesManager {
    val favouriteProductIds: Flow<List<String>>

    fun isFavourite(productId: String): Flow<Boolean>
    fun addFavourite(item: ItemsModel)
    fun removeFavourite(item: ItemsModel)
}
