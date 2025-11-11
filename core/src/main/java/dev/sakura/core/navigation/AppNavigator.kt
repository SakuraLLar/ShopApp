package dev.sakura.core.navigation

import android.app.Activity
import androidx.fragment.app.FragmentManager
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import dev.sakura.models.ItemsModel

interface AppNavigator {
    fun openIntro(context: Context)
    fun openMain(context: Context)
    fun openLogin(fragmentManager: FragmentManager)
    fun openRegistration(fragmentManager: FragmentManager)
    fun openProductDetails(context: Context, item: ItemsModel)
    fun openCart(context: Context)
    fun openFavourites(context: Context)
    fun openOrders(context: Context, items: ArrayList<ItemsModel>)
    fun openProfile(context: Context)
    fun openProfileDetails(activity: Activity, launcher: ActivityResultLauncher<Intent>)
    fun openSettings(activity: Activity, launcher: ActivityResultLauncher<Intent>)
    fun goBack(context: Context)
}
