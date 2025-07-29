package dev.sakura.shopapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
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
import dev.sakura.shopapp.R
import dev.sakura.shopapp.adapter.CartAdapter
import dev.sakura.shopapp.databinding.ActivityCartBinding
import dev.sakura.shopapp.viewModel.CartViewModel

class CartActivity : BaseActivity() {

    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.cartConstrainLayout) { view, windowInsets ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.toolBarCart.updatePadding(top = insets.top)
            WindowInsetsCompat.CONSUMED
        }

        setSupportActionBar(binding.toolBarCart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Корзина"

        setupRecyclerView()
        observeViewModel()
        setupBottomNavigation()
        updateBottomNavSelection(binding.includeBottomNavCart, R.id.nav_cart, this)

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

    private fun setupBottomNavigation() {
        binding.includeBottomNavCart.navExplorer.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
        binding.includeBottomNavCart.navCart.setOnClickListener {
            updateBottomNavSelection(binding.includeBottomNavCart, R.id.nav_cart, this)
        }
        binding.includeBottomNavCart.navOrders.setOnClickListener {
//             startActivity(Intent(this, OrdersActivity::class.java))
//             Toast.makeText(this, "Orders Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
