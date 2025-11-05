package dev.sakura.feature_catalog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.common_ui.R
import dev.sakura.feature_catalog.repository.CatalogRepository
import dev.sakura.models.BrandModel
import dev.sakura.models.ItemsModel
import dev.sakura.models.SliderModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
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

    fun loadBanners() {
        val localBanners = mutableListOf<SliderModel>()

        localBanners.add(SliderModel(R.drawable.banner1))
        localBanners.add(SliderModel(R.drawable.banner2))

        _banner.value = localBanners
    }

    fun loadBrands() {
        val localBrands = listOf(
            BrandModel(title = "Adidas", R.drawable.cat1),
            BrandModel(title = "Nike", R.drawable.cat2),
            BrandModel(title = "Puma", R.drawable.cat3),
            BrandModel(title = "Skechers", R.drawable.cat4),
            BrandModel(title = "Reebok", R.drawable.cat5),
            BrandModel(title = "Lacoste", R.drawable.cat6),
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
