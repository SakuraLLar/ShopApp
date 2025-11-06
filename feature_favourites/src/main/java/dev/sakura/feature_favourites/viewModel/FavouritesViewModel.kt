@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package dev.sakura.feature_favourites.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.feature_catalog.repository.CatalogRepository
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val favouritesManager: FavouritesManager,
    private val catalogRepository: CatalogRepository
) : ViewModel() {

    val favouriteProductIds: Flow<List<String>> = favouritesManager.favouriteProductIds
    val favouriteItems: Flow<List<ItemsModel>> = favouriteProductIds
        .flatMapLatest { ids ->
            catalogRepository.getProductsByIds(ids)
        }

    fun isFavourite(productId: String): Flow<Boolean> {
        return favouritesManager.isFavourite(productId)
    }

    fun toggleFavouriteStatus(item: ItemsModel) {
        viewModelScope.launch {
            val isCurrentlyFavourite = isFavourite(item.resourceId.toString()).first()
            if (isCurrentlyFavourite) {
                favouritesManager.removeFavourite(item)
            } else {
                favouritesManager.addFavourite(item)
            }
        }
    }
}
