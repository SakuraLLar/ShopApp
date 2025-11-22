package dev.sakura.feature_profile.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_orders.adapter.OrdersAdapter
import dev.sakura.feature_orders.adapter.VerticalSpaceItemDecoration
import dev.sakura.feature_orders.viewModel.OrdersViewModel
import dev.sakura.feature_profile.R
import dev.sakura.feature_profile.databinding.ActivityProfileDetailsBinding
import dev.sakura.models.UserModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class ProfileDetailsActivity : BaseActivity() {

    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var sessionProvider: SessionProvider

    private lateinit var binding: ActivityProfileDetailsBinding

    private val authViewModel: AuthViewModel by viewModels()
    private val ordersViewModel: OrdersViewModel by viewModels()

    private lateinit var ordersAdapter: OrdersAdapter

    private val settingsLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                authViewModel.currentUser.value?.let { displayUserData(it) }
                setResult(RESULT_OK)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupClickListeners()
        setupEdgeToEdge()
        setupRecyclerView()
        observeViewModels()
        setupAppBarScrollAnimation()
        initCustomBottomNavigation()
    }

    private fun setupClickListeners() {
        binding.btnBackProfileDetails.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSettings.setOnClickListener {
            appNavigator.openSettings(this, settingsLauncher)
        }
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.btnBackProfileDetails.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 16
            }
            binding.btnSettings.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top + 16
            }
            binding.nestedScrollViewProfileDetails.updatePadding(
                bottom = insets.bottom
            )

            windowInsets
        }
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter { clickedItem ->
            appNavigator.openProductDetails(this, clickedItem)
        }

        val spacingInPixels =
            resources.getDimensionPixelSize(dev.sakura.common_ui.R.dimen.spacing_medium)

        binding.recyclerViewOrders.addItemDecoration(VerticalSpaceItemDecoration(spacingInPixels))
        binding.recyclerViewOrders.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(this@ProfileDetailsActivity)
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModels() {
        authViewModel.currentUser.observe(this) { user ->
            user?.let { displayUserData(it) }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ordersViewModel.orderedItemsList.collect { orders ->
                    if (orders.isEmpty()) {
                        binding.recyclerViewOrders.visibility = View.GONE
                        binding.txtEmptyOrders.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewOrders.visibility = View.VISIBLE
                        binding.txtEmptyOrders.visibility = View.GONE
                        ordersAdapter.submitList(orders)
                    }
                }
            }
        }
    }

    private fun displayUserData(user: UserModel) {
        binding.txtHeaderUserName.text = "${user.firstName} ${user.lastName ?: ""}"
        binding.txtUserEmail.text = user.email
        binding.txtUserPhone.text = user.phoneNumber

        val currentAvatarUri = sessionProvider.getAvatarForCurrentUser()

        if (currentAvatarUri != null) {
            Glide.with(this)
                .load(Uri.parse(currentAvatarUri))
                .placeholder(R.drawable.pic_avatar_placeholder)
                .error(R.drawable.pic_avatar_placeholder)
                .into(binding.imgAvatarProfileDetails)
        } else {
            binding.imgAvatarProfileDetails.setImageResource(R.drawable.pic_avatar_placeholder)
        }

        val currentCoverId = sessionProvider.getCoverForCurrentUser()

        if (currentCoverId != null) {
            binding.imgCoverProfileDetails.setImageResource(currentCoverId)
        } else {
            binding.imgCoverProfileDetails.setImageResource(R.drawable.cover_gradient_lava)
        }
    }

    private fun setupAppBarScrollAnimation() {
        binding.appbarProfileDetails.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                val totalScrollRange = appBarLayout.totalScrollRange
                val alpha = 1.0f - (abs(verticalOffset).toFloat() / totalScrollRange)

                binding.collapsingToolbarContentProfileDetails.alpha = alpha
                binding.collapsingToolbarContentProfileDetails.visibility =
                    if (alpha < 0.1) View.INVISIBLE else View.VISIBLE
                binding.imgAvatarProfileDetails.alpha = alpha
                binding.txtHeaderUserName.alpha = alpha
                binding.btnBackProfileDetails.alpha = alpha
                binding.btnSettings.alpha = alpha
            }
        )
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavProfileDetails

        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navCart.setOnClickListener { appNavigator.openCart(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
