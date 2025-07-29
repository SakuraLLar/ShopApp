package dev.sakura.shopapp.model

import android.os.Parcel
import android.os.Parcelable

data class ItemsModel(
    var resourceId: Int = 0,
    var title: String = "",
    val description: String = "",
    var size: ArrayList<String> = ArrayList(),
    var price: Double = 0.0,
    var rating: Double = 0.0,
    var numberInCart: Int = 0,
    val colorResourceNames: List<String> = emptyList(),

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: ArrayList(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.createStringArrayList() ?: ArrayList(),
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(resourceId)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeStringList(size)
        dest.writeDouble(price)
        dest.writeDouble(rating)
        dest.writeInt(numberInCart)
        dest.writeStringList(colorResourceNames)
    }

    companion object CREATOR : Parcelable.Creator<ItemsModel> {
        override fun createFromParcel(parcel: Parcel): ItemsModel {
            return ItemsModel(parcel)
        }

        override fun newArray(size: Int): Array<ItemsModel?> {
            return arrayOfNulls(size)
        }
    }
}
