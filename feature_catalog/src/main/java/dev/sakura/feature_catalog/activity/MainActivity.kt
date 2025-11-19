package dev.sakura.feature_catalog.activity

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.util.ThemeColorProvider
import dev.sakura.common_ui.view.CustomBottomNavView
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.auth.AuthManager
import dev.sakura.core.auth.SessionProvider
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_catalog.R
import dev.sakura.feature_catalog.adapter.BrandAdapter
import dev.sakura.feature_catalog.adapter.PopularAdapter
import dev.sakura.feature_catalog.adapter.PopularItem
import dev.sakura.feature_catalog.adapter.SliderAdapter
import dev.sakura.feature_catalog.databinding.ActivityMainBinding
import dev.sakura.feature_catalog.viewModel.MainViewModel
import dev.sakura.feature_profile.util.GradientBorderProvider
import dev.sakura.models.SliderModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import dev.sakura.feature_profile.R as ProfileR

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var authManager: AuthManager

    @Inject
    lateinit var sessionProvider: SessionProvider

    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var popularAdapter: PopularAdapter
    private lateinit var brandAdapter: BrandAdapter

    private var isSearchBarInToolbar = false
    private lateinit var searchBarOriginalParams: android.view.ViewGroup.LayoutParams

    private var topInset: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupEdgeToEdge()
        observeCurrentUser()
        initBanner()
        initBrand()
        initPopular()
        setupRoundedCorners()
        setupCustomScrollBehavior()
        initCustomBottomNavigation()
        (binding.includeBottomNavMain
                as? CustomBottomNavView)?.updateSelection(dev.sakura.common_ui.R.id.nav_explorer)
    }

    override fun onResume() {
        super.onResume()
        updateHeaderColor()
    }

    private fun updateHeaderColor() {
        val currentCoverId =
            sessionProvider.getCoverForCurrentUser() ?: ProfileR.drawable.cover_gradient_lava

        val gradientColors = GradientBorderProvider.coverToBorderColorMap[currentCoverId]
            ?: GradientBorderProvider.coverToBorderColorMap[ProfileR.drawable.cover_gradient_lava]!!

        val headerBackground =
            ContextCompat.getDrawable(this, R.drawable.head_island_background_lava)?.mutate()

        if (headerBackground is GradientDrawable) {
            headerBackground.colors = intArrayOf(
                ContextCompat.getColor(this, gradientColors.start),
                ContextCompat.getColor(this, gradientColors.center),
                ContextCompat.getColor(this, gradientColors.end)
            )
            binding.headerViewMain.background = headerBackground
        }
    }

    private fun setupEdgeToEdge() {
        var isPaddingApplied = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            topInset = insets.top

            binding.headerViewMain.setPadding(
                binding.headerViewMain.paddingLeft,
                topInset,
                binding.headerViewMain.paddingRight,
                binding.headerViewMain.paddingBottom
            )
            binding.toolbarMain.setPadding(0, topInset, 0, 0)

            if (!isPaddingApplied) {
                binding.headerContainer.viewTreeObserver.addOnDrawListener {
                    if (binding.headerContainer.height > 0) {
                        binding.nestedScrollViewMain.setPadding(
                            0,
                            binding.headerContainer.height,
                            0,
                            binding.nestedScrollViewMain.paddingBottom
                        )
                        binding.headerContainer.bringToFront()
                        isPaddingApplied = true
                    }
                }
            }
            windowInsets
        }
    }

    private fun observeCurrentUser() {
        authManager.currentUser.observe(this) { user ->
            if (user != null) {
                binding.txtWelcomeMain.text = getString(R.string.main_greeting_user, user.firstName)
            } else {
                binding.txtWelcomeMain.text = getString(R.string.main_greeting_guest)
            }
        }
    }

    private fun setupRoundedCorners() {
        val radius = resources.getDimension(R.dimen.corner_radius_large)
        val colorSurface = ThemeColorProvider.getSurfaceColor(binding.root)
        val shapeModel = ShapeAppearanceModel.builder()
            .setBottomLeftCornerSize(radius)
            .setBottomRightCornerSize(radius)
            .build()
        val shapeDrawable = MaterialShapeDrawable(shapeModel).apply {
            fillColor = ColorStateList.valueOf(colorSurface)
        }

        binding.headerContainer.background = shapeDrawable
    }

    private fun setupCustomScrollBehavior() {
        searchBarOriginalParams = binding.searchBarContainer.layoutParams

        binding.nestedScrollViewMain.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val toolbarHeight =
                binding.headerContainer.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main).height
            if (toolbarHeight == 0 || binding.headerContainer.height == 0) return@setOnScrollChangeListener

            val scrollRange = binding.headerContainer.height - toolbarHeight
            val clampedScrollY = scrollY.coerceIn(0, scrollRange)
            binding.headerContainer.translationY = -clampedScrollY.toFloat()

            val progress = if (scrollRange > 0) {
                (clampedScrollY.toFloat() / scrollRange.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }

            binding.headerViewMain.alpha = 1f - progress
            binding.toolbarMain.alpha = progress

            val isCollapsed = progress > 0.95f
            if (isCollapsed && !isSearchBarInToolbar) {
                (binding.searchBarContainer.parent as? android.view.ViewGroup)?.removeView(binding.searchBarContainer)

                binding.toolbarSearchBarProxyContainer.addView(
                    binding.searchBarContainer,
                    android.widget.FrameLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        (46 * resources.displayMetrics.density).toInt()
                    ).apply {
                        gravity = android.view.Gravity.CENTER_VERTICAL
                    }
                )
                isSearchBarInToolbar = true
            } else if (!isCollapsed && isSearchBarInToolbar) {
                (binding.searchBarContainer.parent as? android.view.ViewGroup)?.removeView(binding.searchBarContainer)
                binding.headerViewMain.addView(binding.searchBarContainer, searchBarOriginalParams)
                isSearchBarInToolbar = false
            }
        }
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
        popularAdapter = PopularAdapter(
            onItemClick = { item ->
                appNavigator.openProductDetails(this, item)
            },
            onFavouriteClick = { item ->
                mainViewModel.toggleFavouriteStatus(item)
            }
        )

        binding.recViewPopularMain.layoutManager = GridLayoutManager(this, 2)
        binding.recViewPopularMain.adapter = popularAdapter

        lifecycleScope.launch {
            mainViewModel.populars.combine(mainViewModel.favouriteProductIds) { populars, favIds ->
                populars.map { item ->
                    PopularItem(
                        model = item,
                        isFavourite = favIds.contains(item.resourceId.toString())
                    )
                }
            }.collect { combinedList ->
                popularAdapter.submitList(combinedList)
            }
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
