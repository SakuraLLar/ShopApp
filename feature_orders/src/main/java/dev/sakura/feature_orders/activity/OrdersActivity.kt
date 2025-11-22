package dev.sakura.feature_orders.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_orders.adapter.OrdersAdapter
import dev.sakura.feature_orders.adapter.VerticalSpaceItemDecoration
import dev.sakura.feature_orders.databinding.ActivityOrdersBinding
import dev.sakura.feature_orders.viewModel.OrdersViewModel
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrdersActivity : AppCompatActivity() {

    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var binding: ActivityOrdersBinding
    private val ordersViewModel: OrdersViewModel by viewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupEdgeToEdge()
        setupRecyclerView()
        observeViewModels()
        initCustomBottomNavigation()
        (binding.includeBottomNavOrders as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_orders)
    }

    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.ordersConstraintLayout) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.guidelineTopOrders.setGuidelineBegin(insets.top)

            val desiredHorizontalPadding =
                resources.getDimensionPixelSize(dev.sakura.common_ui.R.dimen.screen_horizontal_padding)
            val finalPaddingLeft = insets.left + desiredHorizontalPadding
            val finalPaddingRight = insets.right + desiredHorizontalPadding

            binding.recyclerViewOrders.updatePadding(
                left = finalPaddingLeft,
                right = finalPaddingRight,
                bottom = insets.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter { selectedItem: ItemsModel ->
            appNavigator.openProductDetails(this, selectedItem)
        }
        binding.recyclerViewOrders.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.recyclerViewOrders.adapter = ordersAdapter

        val spacingInPixels =
            resources.getDimensionPixelSize(dev.sakura.common_ui.R.dimen.spacing_medium)
        binding.recyclerViewOrders.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))

        val topSpacingInPixels =
            resources.getDimensionPixelSize(dev.sakura.common_ui.R.dimen.spacing_large)
        binding.recyclerViewOrders.addItemDecoration(
            VerticalSpaceItemDecoration(
                spacingInPixels,
                topSpacingInPixels
            )
        )
    }

    private fun observeViewModels() {
        lifecycleScope.launch {
            ordersViewModel.orderedItemsList.collectLatest { orders ->
                if (orders.isNullOrEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                    ordersAdapter.submitList(orders)
                }
            }
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.layoutEmptyOrders.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewOrders.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavOrders

        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navCart.setOnClickListener { appNavigator.openCart(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
