package dev.sakura.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemsModel(
    var resourceId: Int = 0,
    var title: String = "",
    val description: String = "",
    var size: List<String> = emptyList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    val colorResourceNames: List<String> = emptyList(),
) : Parcelable
