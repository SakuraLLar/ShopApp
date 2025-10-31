package dev.sakura.shopapp.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_auth.fragment.LoginDialogFragment
import dev.sakura.feature_auth.fragment.RegisterBottomSheetDialogFragment
import dev.sakura.feature_cart.activity.CartActivity
import dev.sakura.feature_catalog.activity.DetailActivity
import dev.sakura.feature_catalog.activity.MainActivity
import dev.sakura.feature_favourites.activity.FavouritesActivity
import dev.sakura.feature_profile.activity.ProfileActivity
import dev.sakura.feature_profile.activity.ProfileDetailsActivity
import dev.sakura.models.ItemsModel
import dev.sakura.shopapp.activity.IntroActivity
import javax.inject.Inject

class AppNavigatorImpl @Inject constructor() : AppNavigator {
    override fun openIntro(context: Context) {
        val intent = Intent(context, IntroActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        context.startActivity(intent)
    }

    override fun openMain(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        context.startActivity(intent)
    }

    override fun openCart(context: Context) {
        val intent = Intent(context, CartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        context.startActivity(intent)
    }

    override fun openFavourites(context: Context) {
        val intent = Intent(context, FavouritesActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        context.startActivity(intent)
    }

    override fun openProfile(context: Context) {
        val intent = Intent(context, ProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        }
        context.startActivity(intent)
    }

    override fun openProfileDetails(context: Context, avatarUri: String?) {
        val intent = Intent(context, ProfileDetailsActivity::class.java).apply {
            putExtra(ProfileDetailsActivity.EXTRA_AVATAR_URI, avatarUri)
        }
        context.startActivity(intent)
    }

    override fun openLogin(fragmentManager: FragmentManager) {
        val loginDialog = LoginDialogFragment()
        loginDialog.show(fragmentManager, LoginDialogFragment.TAG)
    }

    override fun openRegistration(fragmentManager: FragmentManager) {
        val registerSheet = RegisterBottomSheetDialogFragment.newInstance()
        registerSheet.show(fragmentManager, RegisterBottomSheetDialogFragment.TAG)
    }

    override fun openProductDetails(context: Context, item: ItemsModel) {
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra("object", item)
        context.startActivity(intent)
    }

    override fun goBack(context: Context) {
        if (context is Activity) {
            context.finish()
        }
    }
}
