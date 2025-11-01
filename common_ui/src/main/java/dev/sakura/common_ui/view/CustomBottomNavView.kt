package dev.sakura.common_ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import dev.sakura.common_ui.R
import dev.sakura.common_ui.databinding.LayoutCustomBottomNavBinding

class CustomBottomNavView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: LayoutCustomBottomNavBinding
    private val tabIcons: Map<Int, ImageView>
    private var itemSelectedListener: ((itemId: Int) -> Unit)? = null

    init {
        binding = LayoutCustomBottomNavBinding.inflate(LayoutInflater.from(context), this, true)

        tabIcons = mapOf(
            R.id.nav_explorer to binding.imageViewExplorerIcon,
            R.id.nav_cart to binding.imageViewCartIcon,
            R.id.nav_favourites to binding.imageViewFavouritesIcon,
            R.id.nav_orders to binding.imageViewOrdersIcon,
            R.id.nav_profile to binding.imageViewProfileIcon,
        )

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.navExplorer.setOnClickListener { dispatchItemSelected(R.id.nav_explorer) }
        binding.navCart.setOnClickListener { dispatchItemSelected(R.id.nav_cart) }
        binding.navFavourites.setOnClickListener { dispatchItemSelected(R.id.nav_favourites) }
        binding.navOrders.setOnClickListener { dispatchItemSelected(R.id.nav_orders) }
        binding.navProfile.setOnClickListener { dispatchItemSelected(R.id.nav_profile) }
    }

    private fun dispatchItemSelected(itemId: Int) {
        updateSelection(itemId)
        itemSelectedListener?.invoke(itemId)
    }

    fun updateSelection(selectedContainerId: Int) {
        val activeColor = ContextCompat.getColor(context, R.color.color_bottom_nav_white)
        val inactiveColor = ContextCompat.getColor(context, R.color.color_bottom_nav_light)

        tabIcons.forEach { (containerId, iconView) ->
            if (containerId == selectedContainerId) {
                iconView.setColorFilter(activeColor)
            } else {
                iconView.setColorFilter(inactiveColor)
            }
        }
    }

    fun setOnNavigationItemSelectedListener(listener: (itemId: Int) -> Unit) {
        this.itemSelectedListener = listener
    }
}
