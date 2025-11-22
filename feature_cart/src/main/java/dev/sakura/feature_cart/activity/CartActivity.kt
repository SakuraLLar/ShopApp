package dev.sakura.feature_cart.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope // ⭐ Импортируем lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.orders.OrdersManager
import dev.sakura.feature_cart.adapter.CartAdapter
import dev.sakura.feature_cart.databinding.ActivityCartBinding
import dev.sakura.feature_cart.viewModel.CartViewModel
import dev.sakura.models.ItemsModel
import kotlinx.coroutines.launch // ⭐ Импортируем launch
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var ordersManager: OrdersManager

    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupEdgeToEdge()
        setupRecyclerView()
        observeViewModels()
        setupClickListeners()

        initCustomBottomNavigation()
        (binding.includeBottomNavCart as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_cart)
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.guidelineTopCart.setGuidelineBegin(insets.top)
            windowInsets
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncreaseQuantity = { cartItem ->
                cartViewModel.updateItemQuantity(cartItem.productId, cartItem.quantity + 1)
            },
            onDecreaseQuantity = { cartItem ->
                if (cartItem.quantity > 1) {
                    cartViewModel.updateItemQuantity(cartItem.productId, cartItem.quantity - 1)
                }
            },
            onRemoveItem = { cartItem ->
                cartViewModel.removeItemFromCart(cartItem.productId)
            },
            onToggleSelection = { productId ->
                cartViewModel.toggleItemSelection(productId)
            }
        )
        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun observeViewModels() {
        lifecycleScope.launch {
            cartViewModel.cartItems.collect { items ->
                cartAdapter.submitList(items)
                val isEmpty = items.isEmpty()
                binding.textViewEmptyCart.visibility = if (isEmpty) View.VISIBLE else View.GONE
                binding.layoutSummary.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }
        }

        lifecycleScope.launch {
            cartViewModel.totalPriceOfSelected.collect { price ->
                binding.textViewTotalPrice.text = String.format("$%.2f", price)
            }
        }

        lifecycleScope.launch {
            cartViewModel.isAllSelected.collect { isAllSelected ->
                binding.checkboxSelectAllCart.isChecked = isAllSelected
            }
        }
    }

    private fun setupClickListeners() {
        binding.checkboxSelectAllCart.setOnClickListener {
            cartViewModel.toggleSelectAll()
        }

        binding.buttonCheckout.setOnClickListener {
            val selectedItems = cartViewModel.cartItems.value.filter { it.isSelected }

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Выберите товары для оформления", Toast.LENGTH_SHORT).show()
            } else {
                val itemsToOrder = ArrayList(selectedItems.map { cartItem ->
                    ItemsModel(
                        resourceId = cartItem.imageResourceId ?: 0,
                        title = cartItem.title,
                        price = cartItem.price,
                        numberInCart = cartItem.quantity
                    )
                })

                ordersManager.placeOrder(itemsToOrder) { result ->
                    if (result.isSuccess) {
                        selectedItems.forEach { item ->
                            cartViewModel.removeItemFromCart(item.productId)
                        }
                        Toast.makeText(this, "Заказ успешно оформлен", Toast.LENGTH_SHORT).show()
                        appNavigator.openOrders(this, arrayListOf())
                    } else {
                        Toast.makeText(
                            this,
                            result.exceptionOrNull()?.message ?: "Ошибка оформления заказа",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavCart
        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
