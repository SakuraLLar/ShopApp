package dev.sakura.feature_catalog.repository

import dev.sakura.common_ui.R
import dev.sakura.models.ItemsModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor() {

    private val popularItems: List<ItemsModel> by lazy {
        loadPopulars()
    }

    fun getPopulars(): List<ItemsModel> {
        return popularItems
    }

    fun findPopularById(id: Int): ItemsModel? {
        return popularItems.firstOrNull { it.resourceId == id }
    }

    private fun loadPopulars(): List<ItemsModel> {
        return listOf(
            ItemsModel(
                R.drawable.shoes1,
                title = "Pink singlet",
                description = "Прекрасный выбор классической обуви",
                size = arrayListOf("38", "39", "40", "41", "42", "43"),
                35.0,
                4.6,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n2", "n3", "n4", "n5", "n6")
            ),
            ItemsModel(
                R.drawable.shoes2,
                title = "Jean Coat",
                description = "Стиль и комфорт - это выбор",
                size = arrayListOf("40", "41", "43"),
                55.0,
                4.1,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n2", "n3")
            ),
            ItemsModel(
                R.drawable.shoes3,
                title = "Orange Knit Sweater",
                description = "Лучший выбор для вас.",
                size = arrayListOf("38", "42", "43"),
                75.0,
                4.5,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n3", "n5", "n6")
            ),
            ItemsModel(
                R.drawable.shoes4,
                title = "Plaid shirt",
                description = "Красивые и модные кроссовки для повседневной ходьбы.",
                size = arrayListOf("38", "39"),
                40.0,
                4.2,
                numberInCart = 0,
                colorResourceNames = listOf("n1", "n5", "n6")
            ),
            ItemsModel(
                R.drawable.shoes5,
                title = "Nike Jordans",
                description = "Удобные и красивые кроссовки.",
                size = arrayListOf("37", "39", "40"),
                70.0,
                5.0,
                numberInCart = 0,
                colorResourceNames = listOf("n6")
            ),
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
    }
}
