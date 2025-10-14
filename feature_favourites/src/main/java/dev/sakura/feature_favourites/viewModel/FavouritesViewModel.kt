package dev.sakura.feature_favourites.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.data.FavouritesRepository
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.models.ItemsModel
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favouritesManager: FavouritesManager,
    private val favouritesRepository: FavouritesRepository,
) : ViewModel() {

    val favouritesList: LiveData<List<ItemsModel>> = favouritesRepository.favouritesLiveData

    val favouriteStatusMap: LiveData<Map<Int, Boolean>> = favouritesManager.favouriteStatusMap

    fun toggleFavouriteStatus(item: ItemsModel) {
        favouritesManager.toggleFavouriteStatus(item)
    }

    fun addItemToFavourites(item: ItemsModel) {
        favouritesManager.addItemToFavourites(item)
    }

    fun removeItemFromFavourites(item: ItemsModel) {
        favouritesManager.removeItemFromFavourites(item)
    }

    fun isFavourite(itemId: Int): Boolean {
        return favouritesManager.isFavourite(itemId)
    }
}
