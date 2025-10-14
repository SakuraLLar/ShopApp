package dev.sakura.core.navigation

import android.content.Context
import dev.sakura.models.ItemsModel

interface AppNavigator {
    fun openMain(context: Context)
    fun openLogin(context: Context)
    fun openRegistration(context: Context)
    fun openProductDetails(context: Context, item: ItemsModel)
    fun openCart(context: Context)
    fun openFavourites(context: Context)
    fun openProfile(context: Context)
//    fun openOrders(context: Context)
    fun goBack(context: Context)
}
