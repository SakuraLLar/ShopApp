package dev.sakura.feature_catalog.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.ToolbarFragment
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.cart.CartManager
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.feature_catalog.R
import dev.sakura.feature_catalog.adapter.ColorAdapter
import dev.sakura.feature_catalog.adapter.SizeAdapter
import dev.sakura.feature_catalog.adapter.SliderAdapter
import dev.sakura.feature_catalog.databinding.ActivityDetailBinding
import dev.sakura.feature_catalog.viewModel.DetailViewModel
import dev.sakura.models.ItemsModel
import dev.sakura.models.SliderModel
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    @Inject
    lateinit var cartManager: CartManager

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

//        setupToolbarFragment()
        getInitialDataAndLoadFullProduct()
        observeViewModel()
        initCustomBottomNavigation()
    }

//    private fun setupToolbarFragment() {
//        if (supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) == null) {
//            supportFragmentManager.commit {
//                replace(
//                    R.id.toolbar_fragment_container,
//                    ToolbarFragment.newInstance(title = null, showBackButton = true)
//                )
//            }
//        }
//    }

    private fun getInitialDataAndLoadFullProduct() {
        val receivedItem: ItemsModel? = intent.getParcelableExtra("object")

        if (receivedItem == null) {
            Toast.makeText(this, "Товар не найден", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        detailViewModel.loadProductById(receivedItem.resourceId)
    }

    private fun observeViewModel() {
        detailViewModel.product.observe(this, Observer { fullProduct ->
            if (fullProduct == null) {
                Toast.makeText(this, "Не удалось загрузить информацию о товаре", Toast.LENGTH_LONG)
                    .show()
                finish()
                return@Observer
            }
            updateUi(fullProduct)
        })
    }

    private fun updateUi(item: ItemsModel) {
        binding.txtTitleDetail.text = item.title
        binding.txtPriceDetail.text = String.format("$%.2f", item.price)
        binding.txtRatingDetail.text = "${item.rating} Rating"
        binding.txtDescriptionDetail.text = item.description

        binding.btnAddToCartDetail.setOnClickListener {
            cartManager.addItemToCart(item)
            Toast.makeText(this, "${item.title} добавлен в корзину", Toast.LENGTH_SHORT).show()
        }

        binding.btnGoToCartDetail.setOnClickListener {
            appNavigator.openCart(this)
        }

        setupSlider(item)
        initLists(item)
    }

    private fun setupSlider(item: ItemsModel) {
        val initialSliderItems = ArrayList<SliderModel>()
        if (item.resourceId != 0) {
            initialSliderItems.add(SliderModel(item.resourceId))
        }

        val localSliderAdapter = SliderAdapter(initialSliderItems)
        binding.vpSliderDetail.adapter = localSliderAdapter
        binding.vpSliderDetail.clipToPadding = false
        binding.vpSliderDetail.clipChildren = false
        binding.vpSliderDetail.offscreenPageLimit = 3

        val internalRecyclerView = binding.vpSliderDetail.getChildAt(0) as? RecyclerView
        internalRecyclerView?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        if (initialSliderItems.size > 1) {
            binding.dotIndicatorMain.visibility = View.VISIBLE
            binding.dotIndicatorMain.attachTo(binding.vpSliderDetail)
        } else {
            binding.dotIndicatorMain.visibility = View.GONE
        }
    }

    private fun initLists(item: ItemsModel) {
        if (item.size.isNotEmpty()) {
            binding.txtSizeDetail.visibility = View.VISIBLE
            binding.recViewSizeListDetail.visibility = View.VISIBLE
            binding.recViewSizeListDetail.adapter = SizeAdapter(item.size)
            binding.recViewSizeListDetail.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            binding.txtDescriptionDetail.visibility = View.GONE
            binding.recViewSizeListDetail.visibility = View.GONE
        }

        if (item.colorResourceNames.isNotEmpty()) {
            binding.recViewColorLitDetail.visibility = View.VISIBLE
            binding.recViewColorLitDetail.adapter = ColorAdapter(item.colorResourceNames)
            binding.recViewColorLitDetail.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            binding.recViewColorLitDetail.visibility = View.GONE
        }
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            onBackPressedDispatcher.onBackPressed()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun initCustomBottomNavigation() {
        val bottomNav = binding.includeBottomNavDetail

        bottomNav.navExplorer.setOnClickListener { appNavigator.openMain(this) }
        bottomNav.navCart.setOnClickListener { appNavigator.openCart(this) }
        bottomNav.navFavourites.setOnClickListener { appNavigator.openFavourites(this) }
        bottomNav.navOrders.setOnClickListener { appNavigator.openOrders(this, arrayListOf()) }
        bottomNav.navProfile.setOnClickListener { appNavigator.openProfile(this) }
    }
}
