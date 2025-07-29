package dev.sakura.shopapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.sakura.shopapp.model.BrandModel
import dev.sakura.shopapp.model.ItemsModel
import dev.sakura.shopapp.model.SliderModel
import dev.sakura.shopapp.R

class MainViewModel() : ViewModel() {

    private val _banner = MutableLiveData<List<SliderModel>>()
    val banners: LiveData<List<SliderModel>> = _banner

    private val _brand = MutableLiveData<MutableList<BrandModel>>()
    val brands: LiveData<MutableList<BrandModel>> = _brand

    private val _popular = MutableLiveData<MutableList<ItemsModel>>()
    val populars: LiveData<MutableList<ItemsModel>> = _popular

    fun loadBanners() {
        val localBanners = mutableListOf<SliderModel>()

        localBanners.add(SliderModel(R.drawable.banner1))
        localBanners.add(SliderModel(R.drawable.banner2))

        _banner.value = localBanners
    }

    fun loadBrands() {
        val localBrands = mutableListOf<BrandModel>()

        localBrands.add(BrandModel(title = "Adidas", R.drawable.cat1))
        localBrands.add(BrandModel(title = "Nike", R.drawable.cat2))
        localBrands.add(BrandModel(title = "Puma", R.drawable.cat3))
        localBrands.add(BrandModel(title = "Skechers", R.drawable.cat4))
        localBrands.add(BrandModel(title = "Reebok", R.drawable.cat5))
        localBrands.add(BrandModel(title = "Lacoste", R.drawable.cat6))

        _brand.value = localBrands
    }

    fun loadPopulars() {
        val localPopulars = mutableListOf<ItemsModel>()

        localPopulars.add(
            ItemsModel(
                R.drawable.shoes1,
                title = "Pink singlet",
                description = "A beautiful pink singlet for sunny days.",
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
                description = "Stylish jean coat for a casual look.",
                size = arrayListOf("40", "41", "43"),
                55.0,
                4.1,
                numberInCart = 0,
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes3,
                title = "Orange Knit Sweater",
                description = "Warm and cozy orange knit sweater.",
                size = arrayListOf("38", "42", "43"),
                75.0,
                4.5,
                numberInCart = 0,
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes4,
                title = "Plaid shirt",
                description = "Classic plaid shirt for any occasion.",
                size = arrayListOf("38", "39"),
                40.0,
                4.2,
                numberInCart = 0,
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes5,
                title = "Nike Jordans",
                description = "Сomfortable and beautiful sneakers",
                size = arrayListOf("37", "39", "40"),
                70.0,
                5.0,
                numberInCart = 0,
            )
        )
        localPopulars.add(
            ItemsModel(
                R.drawable.shoes6,
                title = "Multi-colored palette",
                description = "Bright and stylish sneakers",
                size = arrayListOf("40", "44"),
                28.0,
                4.7,
                numberInCart = 0,
            )
        )

        _popular.value = localPopulars
    }
}
