package dev.sakura.shopapp.activity

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.sakura.shopapp.R
import dev.sakura.shopapp.databinding.LayoutCustomBottomNavBinding

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    fun updateBottomNavSelection(
        bottomNavBinding: LayoutCustomBottomNavBinding,
        selectedContainerId: Int,
        context: Context,
    ) {
        val activeColor = ContextCompat.getColor(context, R.color.black)
        val inactiveColor = ContextCompat.getColor(context, R.color.darkGrey)

        val tabs = mapOf(
            R.id.nav_explorer to (bottomNavBinding.imageViewExplorerIcon to bottomNavBinding.textViewExplorerLabel),
            R.id.nav_cart to (bottomNavBinding.imageViewCartIcon to bottomNavBinding.textViewCartLabel),
            R.id.nav_orders to (bottomNavBinding.imageViewOrdersIcon to bottomNavBinding.textViewOrdersLabel),
            R.id.nav_wishlist to (bottomNavBinding.imageViewWishlistIcon to bottomNavBinding.textViewWishlistLabel),
            R.id.nav_profile to (bottomNavBinding.imageViewProfileIcon to bottomNavBinding.textViewProfileLabel),
        )

        tabs.forEach { (containerId, views) ->
            val (icon, label) = views
            if (containerId == selectedContainerId) {
                icon.setColorFilter(activeColor)
                label.setTextColor(activeColor)
            } else {
                icon.setColorFilter(inactiveColor)
                label.setTextColor(inactiveColor)
            }
        }
    }
}
