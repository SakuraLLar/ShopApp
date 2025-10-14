package dev.sakura.feature_catalog.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.sakura.common_ui.R
import dev.sakura.models.BrandModel
import dev.sakura.models.ItemsModel
import dev.sakura.models.SliderModel

class MainViewModel() : ViewModel() {

    private val _banner = MutableLiveData<List<SliderModel>>()
    val banners: LiveData<List<SliderModel>> = _banner

    private val _brandItems = MutableLiveData<List<BrandModel>>()
    val brands: LiveData<List<BrandModel>> = _brandItems

    private val _popular = MutableLiveData<MutableList<ItemsModel>>()
    val populars: LiveData<MutableList<ItemsModel>> = _popular

    private val _selectedBrand = MutableLiveData<BrandModel?>()
    val selectedBrand: LiveData<BrandModel?> = _selectedBrand

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

    fun loadPopulars() {
        val localPopulars = mutableListOf<ItemsModel>()

        localPopulars.add(
            ItemsModel(
                R.drawable.shoes1,
                title = "Pink singlet",
                description = "Прекрасный выбор классической обуви",
                size = arrayListOf("38", "39", "40", "41", "42", "43"),
                35.0,
                4.6,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n2", "n3", "n4", "n5", "n6")
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes2,
                title = "Jean Coat",
                description = "Стиль и комфорт - это выбор",
                size = arrayListOf("40", "41", "43"),
                55.0,
                4.1,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n2", "n3")
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes3,
                title = "Orange Knit Sweater",
                description = "Лучший выбор для вас.",
                size = arrayListOf("38", "42", "43"),
                75.0,
                4.5,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n3", "n5", "n6")
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes4,
                title = "Plaid shirt",
                description = "Красивые и модные кроссовки для повседневной ходьбы.",
                size = arrayListOf("38", "39"),
                40.0,
                4.2,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n5", "n6")
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes5,
                title = "Nike Jordans",
                description = "Удобные и красивые кроссовки.",
                size = arrayListOf("37", "39", "40"),
                70.0,
                5.0,
                numberInCart = 0,
                colorResourceNames = listOf("n6")
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes6,
                title = "Multi-colored palette",
                description = "Яркие и стильные кроссовки.",
                size = arrayListOf("40", "44"),
                28.0,
                4.7,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n2", "n3")
            )
        )

        _popular.value = localPopulars
    }

    fun onBrandSelected(brand: BrandModel) {
        if (_selectedBrand.value?.resourceId == brand.resourceId) {
            _selectedBrand.value = null
        } else {
            _selectedBrand.value = brand
        }
    }
}
