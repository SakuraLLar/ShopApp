package dev.sakura.shopapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import dev.sakura.shopapp.R
import dev.sakura.shopapp.adapter.BrandAdapter
import dev.sakura.shopapp.adapter.PopularAdapter
import dev.sakura.shopapp.adapter.SliderAdapter
import dev.sakura.shopapp.databinding.ActivityMainBinding
import dev.sakura.shopapp.model.SliderModel
import dev.sakura.shopapp.viewModel.MainViewModel

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBanner()
        initBrand()
        initPopular()
        initCustomBottomNavigation()
        updateBottomNavSelection(binding.includedBottomNavMain, R.id.nav_explorer, this)
    }

    private fun initBanner() {
        binding.progressBarBanner.visibility = View.VISIBLE
        viewModel.banners.observe(this, Observer { items ->
            banners(items)
            binding.progressBarBanner.visibility = View.GONE
        })
        viewModel.loadBanners()
    }

    private fun banners(images: List<SliderModel>) {
        val mutableImages = images.toMutableList()
        val sliderAdapter = SliderAdapter(mutableImages, binding.viewpagerSlider)
        binding.viewpagerSlider.adapter = sliderAdapter
        binding.viewpagerSlider.clipToPadding = false
        binding.viewpagerSlider.clipChildren = false
        binding.viewpagerSlider.offscreenPageLimit = 3

        if (mutableImages.isNotEmpty()) {
            if (binding.viewpagerSlider.childCount > 0) {
                (binding.viewpagerSlider.getChildAt(0) as? RecyclerView)?.overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER
            }
        }

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
        }

        binding.viewpagerSlider.setPageTransformer(compositePageTransformer)
        if (images.size > 1) {
            binding.dotIndicator.visibility = View.VISIBLE
            binding.dotIndicator.attachTo(binding.viewpagerSlider)
        } else {
            binding.dotIndicator.visibility = View.GONE
        }
    }

    private fun initBrand() {
        binding.progressBarBrand.visibility = View.VISIBLE
        if (binding.viewBrand.layoutManager == null) {
            binding.viewBrand.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }
        viewModel.brands.observe(this, Observer { brandList ->
            binding.progressBarBrand.visibility = View.GONE
            if (brandList != null) {
                if (binding.viewBrand.adapter == null) {
                    binding.viewBrand.adapter = BrandAdapter(brandList)
                } else {
                    (binding.viewBrand.adapter as BrandAdapter).updateData(brandList)
                }
            }
        })
        viewModel.loadBrands()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        if (binding.viewPopular.layoutManager == null) {
            binding.viewPopular.layoutManager = GridLayoutManager(this, 2)
        }
        viewModel.populars.observe(this, Observer { popularList ->
            binding.progressBarPopular.visibility = View.GONE
            if (popularList != null) {
                if (binding.viewPopular.adapter == null) {
                    binding.viewPopular.adapter = PopularAdapter(popularList)
                } else {
                    (binding.viewPopular.adapter as PopularAdapter).updateDataWith(popularList)
                }
            }
        })
        viewModel.loadPopulars()
    }

    private fun initCustomBottomNavigation() {
        binding.includedBottomNavMain.navExplorer.setOnClickListener {
            updateBottomNavSelection(binding.includedBottomNavMain, R.id.nav_explorer, this)
        }
        binding.includedBottomNavMain.navCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        binding.includedBottomNavMain.navOrders.setOnClickListener {
            // startActivity(Intent(this, OrdersActivity::class.java))
        }
        binding.includedBottomNavMain.navWishlist.setOnClickListener {
            // startActivity(Intent(this, WishlistActivity::class.java))
        }
        binding.includedBottomNavMain.navProfile.setOnClickListener {
            // startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
