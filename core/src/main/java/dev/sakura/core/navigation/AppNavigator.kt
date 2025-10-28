package dev.sakura.core.navigation

import androidx.fragment.app.FragmentManager
import android.content.Context
import dev.sakura.models.ItemsModel

interface AppNavigator {
    fun openIntro(context: Context)
    fun openMain(context: Context)
    fun openLogin(fragmentManager: FragmentManager)
    fun openRegistration(fragmentManager: FragmentManager)
    fun openProductDetails(context: Context, item: ItemsModel)
    fun openCart(context: Context)
    fun openFavourites(context: Context)
    fun openProfile(context: Context)
//    fun openOrders(context: Context)
    fun goBack(context: Context)
}
