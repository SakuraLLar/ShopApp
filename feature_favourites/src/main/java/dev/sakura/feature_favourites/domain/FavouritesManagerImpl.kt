package dev.sakura.feature_favourites.domain

import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.data.repository.FavouritesRepositoryImpl
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesManagerImpl @Inject constructor(
    private val favouritesRepository: FavouritesRepositoryImpl,
) : FavouritesManager {

    private val managerScope = CoroutineScope(Dispatchers.IO)

    override val favouriteProductIds: Flow<List<String>>
        get() = favouritesRepository.favouriteProductIds

    override fun isFavourite(productId: String): Flow<Boolean> {
        return favouritesRepository.isFavourite(productId)
    }

    override fun addFavourite(item: ItemsModel) {
        managerScope.launch {
            favouritesRepository.addFavourite(item)
        }
    }

    override fun removeFavourite(item: ItemsModel) {
        managerScope.launch {
            favouritesRepository.removeFavourite(item)
        }
    }
}
