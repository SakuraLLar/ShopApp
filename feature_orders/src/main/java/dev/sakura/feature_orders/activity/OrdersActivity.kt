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
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_orders.adapter.OrdersAdapter
import dev.sakura.feature_orders.databinding.ActivityOrdersBinding
import dev.sakura.feature_orders.viewModel.OrdersViewModel
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

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.ordersConstraintLayout) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolBarOrders.updatePadding(top = insets.top)
            binding.recyclerViewOrders.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        initCustomBottomNavigation()
        (binding.includeBottomNavOrders as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_orders)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolBarOrders)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter { selectedItem ->
            appNavigator.openProductDetails(this, selectedItem)
        }
        binding.recyclerViewOrders.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        binding.recyclerViewOrders.adapter = ordersAdapter
    }

    private fun observeViewModel() {
        ordersViewModel.orderedItemsList.observe(this, Observer { items ->
            if (items.isNullOrEmpty()) {
                showEmptyState(true)
            } else {
                showEmptyState(false)
                ordersAdapter.submitList(items)
            }
        })
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
