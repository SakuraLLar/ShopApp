package dev.sakura.feature_catalog.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_auth.viewModel.AuthViewModel
import dev.sakura.feature_catalog.R
import dev.sakura.feature_catalog.adapter.BrandAdapter
import dev.sakura.feature_catalog.adapter.PopularAdapter
import dev.sakura.feature_catalog.adapter.SliderAdapter
import dev.sakura.feature_catalog.databinding.ActivityMainBinding
import dev.sakura.feature_catalog.viewModel.MainViewModel
import dev.sakura.models.SliderModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var favouritesManager: FavouritesManager

    private lateinit var binding: ActivityMainBinding
    private lateinit var popularAdapter: PopularAdapter

    @Inject
    lateinit var sessionProvider: SessionProvider

    private lateinit var brandAdapter: BrandAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupUserInfo()
        observeCurrentUser()

        initBanner()
        initBrand()
        initPopular()
        observeFavourites()

        initCustomBottomNavigation()
        (binding.includeBottomNavMain
                as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_explorer)
    }

    override fun onResume() {
        super.onResume()
        setupUserInfo()
    }

    private fun setupUserInfo() {
        if (sessionProvider.isLoggedIn()) {
            sessionProvider.getCurrentUserId()?.let { userId ->
                authViewModel.loadCurrentUserFromSession(userId)
            }
        } else {
            binding.txtWelcomeMain.text = getString(R.string.main_greeting)
            binding.txtNameMain.text = getString(R.string.guest_name)
        }
    }

    private fun observeCurrentUser() {
        authViewModel.currentUser.observe(this, Observer { user ->
            if (user != null) {
                binding.txtWelcomeMain.text = getString(R.string.main_greeting_user, user.firstName)
                binding.txtNameMain.visibility = View.GONE
            } else {
                if (sessionProvider.isLoggedIn()) {
                    binding.txtWelcomeMain.text = getString(R.string.main_greeting)
                    binding.txtNameMain.text = "Ошибка загрузки."
                    binding.txtNameMain.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initBanner() {
        mainViewModel.banners.observe(this, Observer { items ->
            banners(items)
        })
        mainViewModel.loadBanners()
    }

    private fun banners(images: List<SliderModel>) {
        val mutableImages = images.toMutableList()
        val sliderAdapter = SliderAdapter(images.toMutableList())
        binding.vpSliderMain.adapter = sliderAdapter
        binding.vpSliderMain.clipToPadding = false
        binding.vpSliderMain.clipChildren = false
        binding.vpSliderMain.offscreenPageLimit = 3

        if (mutableImages.isNotEmpty()) {
            if (binding.vpSliderMain.childCount > 0) {
                (binding.vpSliderMain.getChildAt(0) as? RecyclerView)?.overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER
            }
        }

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }

        binding.vpSliderMain.setPageTransformer(compositePageTransformer)
        if (images.size > 1) {
            binding.dotIndicatorMain.visibility = View.VISIBLE
            binding.dotIndicatorMain.attachTo(binding.vpSliderMain)
        } else {
            binding.dotIndicatorMain.visibility = View.GONE
        }
    }

    private fun initBrand() {
        brandAdapter = BrandAdapter { selectedBrand ->
            mainViewModel.onBrandSelected(selectedBrand)
        }
        binding.recViewBrandMain.adapter = brandAdapter
        binding.recViewBrandMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mainViewModel.brands.observe(this) { brandList ->
            val selectedId = mainViewModel.selectedBrand.value?.resourceId
            brandAdapter.submitList(brandList.map {
                BrandAdapter.BrandItem(
                    it,
                    it.resourceId == selectedId
                )
            })
        }

        mainViewModel.selectedBrand.observe(this) { selectedBrand ->
            val currentList = mainViewModel.brands.value ?: emptyList()
            brandAdapter.submitList(currentList.map {
                BrandAdapter.BrandItem(it, it.resourceId == selectedBrand?.resourceId)
            })
        }
        mainViewModel.loadBrands()
    }

    private fun initPopular() {
        popularAdapter = PopularAdapter(favouritesManager)

        binding.recViewPopularMain.layoutManager = GridLayoutManager(this, 2)
        binding.recViewPopularMain.adapter = popularAdapter

        mainViewModel.populars.observe(this, Observer { popularList ->
            if (popularList != null) {
                popularAdapter.submitList(popularList)
            }
        })
        mainViewModel.loadPopulars()
    }

    private fun observeFavourites() {
        favouritesManager.favouriteStatusMap.observe(this) { statusMap ->
            popularAdapter.setFavouritesStatusMap(statusMap)
        }
    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavMain

        bottomNav.navCart.setOnClickListener { appNavigator.openCart(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
