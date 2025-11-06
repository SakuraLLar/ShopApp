@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
package dev.sakura.feature_catalog.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.feature_catalog.repository.CatalogRepository
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val favouritesManager: FavouritesManager,
) : ViewModel() {

    private val _productId = MutableStateFlow<Int?>(null)

    val product: StateFlow<ItemsModel?> = _productId.filterNotNull().flatMapLatest { id ->
        catalogRepository.findPopularById(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isFavourites: StateFlow<Boolean> = _productId.filterNotNull().flatMapLatest { id ->
        favouritesManager.isFavourite(id.toString())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun loadProductById(productId: Int) {
        _productId.value = productId
    }

    fun toggleFavourite() {
        viewModelScope.launch {
            val currentProduct = product.value ?: return@launch
            val isCurrentlyFavourite = isFavourites.value
            if (isCurrentlyFavourite) {
                favouritesManager.removeFavourite(currentProduct)
            } else {
                favouritesManager.addFavourite(currentProduct)
            }
        }
    }
}
