package dev.sakura.shopapp.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.get
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import dev.sakura.shopapp.Adapter.BrandAdapter
import dev.sakura.shopapp.Adapter.PopularAdapter
import dev.sakura.shopapp.Adapter.SliderAdapter
import dev.sakura.shopapp.Model.SliderModel
import dev.sakura.shopapp.ViewModel.MainViewModel
import dev.sakura.shopapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var brandAdapter: BrandAdapter
    private lateinit var popularAdapter: PopularAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBrandRecyclerView()
        setupPopularRecyclerView()

        initBanner()
        initBrand()
        initPopular()
    }

    private fun setupPopularRecyclerView() {
        binding.viewPopular.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupBrandRecyclerView() {
        binding.viewBrand.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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

        if (images.isNotEmpty()) {
            binding.viewpagerSlider.get(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
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
        viewModel.brands.observe(this, Observer { brandList ->
            binding.progressBarBrand.visibility = View.GONE

            if (brandList != null) {
                if (!::brandAdapter.isInitialized) {
                    brandAdapter = BrandAdapter(brandList)
                    binding.viewBrand.adapter = brandAdapter
                } else {
                    brandAdapter.updateData(brandList)
                }
            }
        })
        viewModel.loadBrands()
    }

    private fun initPopular() {
        binding.progressBarPopular.visibility = View.VISIBLE
        viewModel.populars.observe(this, Observer { popularList ->
            binding.progressBarPopular.visibility = View.GONE

            if (popularList != null) {
                val currentPopularAdapter = PopularAdapter(popularList)
                binding.viewPopular.adapter = currentPopularAdapter
            }
        })
        viewModel.loadPopulars()
    }
}
