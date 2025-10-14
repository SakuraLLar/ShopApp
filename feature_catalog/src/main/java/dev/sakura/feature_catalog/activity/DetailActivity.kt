package dev.sakura.feature_catalog.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import dev.sakura.common_ui.ToolbarFragment
import dev.sakura.core.activity.BaseActivity
import dev.sakura.core.navigation.AppNavigator
import dev.sakura.core.util.CartManager
import dev.sakura.feature_catalog.R
import dev.sakura.feature_catalog.adapter.ColorAdapter
import dev.sakura.feature_catalog.adapter.SizeAdapter
import dev.sakura.feature_catalog.adapter.SliderAdapter
import dev.sakura.feature_catalog.databinding.ActivityDetailBinding
import dev.sakura.models.ItemsModel
import dev.sakura.models.SliderModel
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    @Inject
    lateinit var appNavigator: AppNavigator

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private lateinit var colorAdapter: ColorAdapter

    @Inject
    lateinit var cartManager: CartManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupToolbarFragment()
        getBundleAndSetupViews()
        initCustomBottomNavigation()
    }

    private fun setupToolbarFragment() {
        if (supportFragmentManager.findFragmentById(R.id.toolbar_fragment_container) == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.toolbar_fragment_container,
                    ToolbarFragment.newInstance(title = null, showBackButton = true)
                )
            }
        }
    }

    private fun getBundleAndSetupViews() {
        val receivedItem: ItemsModel? = intent.getParcelableExtra("object")
        if (receivedItem == null) {
            Toast.makeText(this, "Товар не найден", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        item = receivedItem

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

        setupSlider()
        initLists()
    }

    private fun setupSlider() {
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

    private fun initLists() {
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
            colorAdapter = ColorAdapter(item.colorResourceNames)
            binding.recViewColorLitDetail.adapter = colorAdapter
            binding.recViewColorLitDetail.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            binding.recViewColorLitDetail.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            appNavigator.openMain(this)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initCustomBottomNavigation() {
        binding.includeBottomNavDetail.navExplorer.setOnClickListener {
            appNavigator.openMain(this)
        }
        binding.includeBottomNavDetail.navCart.setOnClickListener {
            appNavigator.openCart(this)
        }
        binding.includeBottomNavDetail.navFavourites.setOnClickListener {
            appNavigator.openFavourites(this)
        }
        binding.includeBottomNavDetail.navOrders.setOnClickListener {
            Toast.makeText(this, "Orders Clicked (Not Implemented)", Toast.LENGTH_SHORT).show()
        }
        binding.includeBottomNavDetail.navProfile.setOnClickListener {
            appNavigator.openProfile(this)
        }
    }
}
