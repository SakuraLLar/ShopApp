package dev.sakura.shopapp.Model

data class ItemModel(
    val resourceId: Int = 0,
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
)
