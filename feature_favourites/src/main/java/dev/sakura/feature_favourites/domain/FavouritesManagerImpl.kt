package dev.sakura.feature_favourites.domain

import androidx.lifecycle.LiveData
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.data.repository.FavouritesRepositoryImpl
import dev.sakura.models.ItemsModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouritesManagerImpl @Inject constructor(
    private val favouritesRepository: FavouritesRepositoryImpl
) : FavouritesManager {

    override fun addItemToFavourites(item: ItemsModel) {
        favouritesRepository.addItem(item)
    }

    override fun removeItemFromFavourites(item: ItemsModel) {
        favouritesRepository.removeItem(item)
    }

    override fun toggleFavouriteStatus(item: ItemsModel) {
        favouritesRepository.toggleFavouriteStatus(item)
    }

    override val favouriteStatusMap: LiveData<Map<Int, Boolean>>
        get() = favouritesRepository.isFavouritesStatusLiveData

    override fun isFavourite(itemId: Int): Boolean {
        return favouritesRepository.isItemFavourite(itemId)
    }

}
