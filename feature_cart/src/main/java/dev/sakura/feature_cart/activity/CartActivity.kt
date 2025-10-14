package dev.sakura.feature_cart.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_cart.adapter.CartAdapter
import dev.sakura.feature_cart.databinding.ActivityCartBinding
import dev.sakura.feature_cart.viewModel.CartViewModel
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setSupportActionBar(binding.toolBarCart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Корзина"

        setupRecyclerView()
        observeViewModel()
        initCustomBottomNavigation()

        (binding.includeBottomNavCart as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_cart)

        binding.buttonCheckout.setOnClickListener {
            if (cartViewModel.allCartItems.value.isNullOrEmpty()) {
                Toast.makeText(this, "Пусто", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Оформление заказа...", Toast.LENGTH_SHORT).show()
            }
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
        bottomNav.navOrders.setOnClickListener {
            Toast.makeText(this, "Orders Clicked (Not implemented)", Toast.LENGTH_SHORT).show()
        }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
