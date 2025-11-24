package dev.sakura.feature_catalog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.common_ui.R
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.feature_catalog.repository.CatalogRepository
import dev.sakura.models.BrandModel
import dev.sakura.models.ItemsModel
import dev.sakura.models.SliderModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    catalogRepository: CatalogRepository,
    private val favouritesManager: FavouritesManager,
) : ViewModel() {

    private val _banner = MutableLiveData<List<SliderModel>>()
    val banners: LiveData<List<SliderModel>> = _banner

    private val _brandItems = MutableLiveData<List<BrandModel>>()
    val brands: LiveData<List<BrandModel>> = _brandItems

    private val _selectedBrand = MutableLiveData<BrandModel?>()
    val selectedBrand: LiveData<BrandModel?> = _selectedBrand

    val populars: StateFlow<List<ItemsModel>> = catalogRepository.getPopulars()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val favouriteProductIds: Flow<List<String>> = favouritesManager.favouriteProductIds

    fun toggleFavouriteStatus(item: ItemsModel) {
        viewModelScope.launch {
            val isCurrentlyFavourite = favouritesManager.isFavourite(item.resourceId.toString()).first()

            if (isCurrentlyFavourite) {
                favouritesManager.removeFavourite(item)
            } else {
                favouritesManager.addFavourite(item)
            }
        }
    }

    fun loadBanners() {
        val localBanners = mutableListOf<SliderModel>()

        localBanners.add(SliderModel(R.drawable.pic_banner1))
        localBanners.add(SliderModel(R.drawable.pic_banner2))

        _banner.value = localBanners
    }

    fun loadBrands() {
        val localBrands = listOf(
            BrandModel(title = "Adidas", R.drawable.pic_brand_adidas),
            BrandModel(title = "Nike", R.drawable.pic_brand_nike),
            BrandModel(title = "Puma", R.drawable.pic_brand_puma),
            BrandModel(title = "Skechers", R.drawable.pic_brand_skechers),
            BrandModel(title = "Reebok", R.drawable.pic_brand_reebok),
            BrandModel(title = "Lacoste", R.drawable.pic_brand_lacoste),
        )
        _brandItems.value = localBrands
    }

    fun onBrandSelected(brand: BrandModel) {
        if (_selectedBrand.value?.resourceId == brand.resourceId) {
            _selectedBrand.value = null
        } else {
            _selectedBrand.value = brand
        }
    }
}
