package dev.sakura.feature_favourites.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_favourites.adapter.FavouritesAdapter
import dev.sakura.feature_favourites.databinding.ActivityFavouritesBinding
import dev.sakura.feature_favourites.viewModel.FavouritesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var binding: ActivityFavouritesBinding
    private val favouritesViewModel: FavouritesViewModel by viewModels()
    private lateinit var favouritesAdapter: FavouritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupEdgeToEdge()
        setupRecyclerView()
        observeViewModel()

        initCustomBottomNavigation()
        (binding.includeBottomNavFavourites as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_favourites)
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.guidelineTopFavourites.setGuidelineBegin(insets.top)
            windowInsets
        }
    }

    private fun setupRecyclerView() {
        favouritesAdapter = FavouritesAdapter(
            onItemClicked = { item ->
                appNavigator.openProductDetails(this, item)
            },
            onRemoveFromFavouritesClicked = { item ->
                favouritesViewModel.toggleFavouriteStatus(item)
                Toast.makeText(this, "${item.title} удалено из избранного", Toast.LENGTH_SHORT)
                    .show()
            }
        )
        binding.recyclerViewFavourites.adapter = favouritesAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            favouritesViewModel.favouriteItems.collectLatest { items ->
                if (items.isNullOrEmpty()) {
                    showEmptyFavouritesState(true)
                } else {
                    showEmptyFavouritesState(false)
                    favouritesAdapter.submitList(items)
                }
            }
        }
    }

    private fun showEmptyFavouritesState(isEmpty: Boolean) {
        binding.layoutEmptyFavourites.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewFavourites.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavFavourites

        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navCart.setOnClickListener { appNavigator.openCart(this) }
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
