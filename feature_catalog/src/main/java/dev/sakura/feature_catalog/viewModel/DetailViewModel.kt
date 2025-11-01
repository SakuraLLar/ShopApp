package dev.sakura.feature_catalog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sakura.feature_catalog.repository.CatalogRepository
import dev.sakura.models.ItemsModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
) : ViewModel() {

    private val _product = MutableLiveData<ItemsModel?>()
    val product: LiveData<ItemsModel?> = _product

    fun loadProductById(productId: Int) {
        val foundProduct = catalogRepository.findPopularById(productId)
        _product.value = foundProduct
    }
}
