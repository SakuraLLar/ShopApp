package dev.sakura.feature_cart.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.orders.OrdersManager
import dev.sakura.feature_cart.adapter.CartAdapter
import dev.sakura.feature_cart.databinding.ActivityCartBinding
import dev.sakura.feature_cart.viewModel.CartViewModel
import dev.sakura.models.CartItem
import dev.sakura.models.ItemsModel
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

        setupRecyclerView()
        observeViewModel()

        binding.buttonCheckout.setOnClickListener {
            val itemsInCart = cartViewModel.allCartItems.value
            if (itemsInCart.isNullOrEmpty()) {
                Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            } else {
                val itemsToOrder = ArrayList(itemsInCart.map { it.toItemsModel() })

                ordersManager.placeOrder(itemsToOrder)

                cartViewModel.clearCartViewModelAction()
                Toast.makeText(this, "Заказ успешно оформлен", Toast.LENGTH_SHORT).show()
                appNavigator.openOrders(this, arrayListOf())
            }
        }

        initCustomBottomNavigation()
        (binding.includeBottomNavCart as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_cart)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncreaseQuantity = { cartItem ->
                cartViewModel.updateItemQuantity(cartItem.productId, cartItem.quantity + 1)
            },
            onDecreaseQuantity = { cartItem ->
                if (cartItem.quantity > 1) {
                    cartViewModel.updateItemQuantity(cartItem.productId, cartItem.quantity - 1)
                } else {
                    cartViewModel.removeItemFromCart(cartItem.productId)
                }
            },
            onRemoveItem = { cartItem ->
                cartViewModel.removeItemFromCart(cartItem.productId)
            }
        )
        binding.recyclerViewCart.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun observeViewModel() {
        cartViewModel.allCartItems.observe(this, Observer { cartItems ->
            if (cartItems.isNullOrEmpty()) {
                binding.recyclerViewCart.visibility = View.GONE
                binding.textViewEmptyCart.visibility = View.VISIBLE
                binding.layoutSummary.visibility = View.GONE
            } else {
                binding.recyclerViewCart.visibility = View.VISIBLE
                binding.textViewEmptyCart.visibility = View.GONE
                binding.layoutSummary.visibility = View.VISIBLE
            }
            cartAdapter.submitList(cartItems)
        })

        cartViewModel.cartTotalPrice.observe(this, Observer { totalPrice ->
            binding.textViewTotalPrice.text = String.format("$%.2f", totalPrice ?: 0.0)
        })
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavCart

        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
