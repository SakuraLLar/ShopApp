package dev.sakura.core.data

import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {
    val favouriteProductIds: Flow<List<String>>

    fun isFavourite(productId: String): Flow<Boolean>

    suspend fun addFavourite(item: ItemsModel)
    suspend fun removeFavourite(item: ItemsModel)
}
