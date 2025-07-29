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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.sakura.shopapp.adapter.ColorAdapter
import dev.sakura.shopapp.adapter.SizeAdapter
import dev.sakura.shopapp.adapter.SliderAdapter
import dev.sakura.shopapp.helper.ManagmentCart
import dev.sakura.shopapp.model.ItemsModel
import dev.sakura.shopapp.model.SliderModel
import dev.sakura.shopapp.R
import dev.sakura.shopapp.databinding.ActivityDetailBinding
import dev.sakura.shopapp.viewModel.CartViewModel

class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private var numberOder = 1
    private lateinit var colorAdapter: ColorAdapter

    private val cartViewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainCoordinatorRoot) { view, windowInsets ->
            val insets =
                windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            binding.toolbarDetail.updatePadding(top = insets.top)
            binding.nestedScrollViewContainer.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        setupToolbar()
        getBundleAndSetupViews()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun getBundleAndSetupViews() {
        val receivedItem: ItemsModel? = intent.getParcelableExtra("object")
        if (receivedItem == null) {
            Toast.makeText(this, "Товвар не найден", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        item = receivedItem

        binding.txtTitleDetail.text = item.title
        binding.txtPriceDetail.text = String.format("$%.2f", item.price)
        binding.txtRatingDetail.text = "${item.rating} Rating"
        binding.txtDescription.text = item.description

        binding.btnAddToCart.setOnClickListener {
            cartViewModel.addItemToCart(item, numberOder)
            Toast.makeText(this, "${item.title} добавлен в корзину", Toast.LENGTH_SHORT).show()
        }

        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        binding.txtQuantityDetail.text = numberOder.toString()
        binding.btnIncreaseQuantityDetail.setOnClickListener {
            numberOder++
            binding.txtQuantityDetail.text = numberOder.toString()

        }
        binding.btnDecreaseQuantityDetail.setOnClickListener {
            if (numberOder > 1) {
                numberOder--
                binding.txtQuantityDetail.text = numberOder.toString()
            }
        }

        setupSlider()
        initLists()
    }

    private fun setupSlider() {
        val initialSliderItems = ArrayList<SliderModel>()
        if (item.resourceId != 0) {
            initialSliderItems.add(SliderModel(item.resourceId))
        } else {
//            initialSliderItems.add(SliderModel(R.drawable.placeholder_image))
        }

        val localSliderAdapter = SliderAdapter(initialSliderItems, binding.sliderDetail)
        binding.sliderDetail.adapter = localSliderAdapter
        binding.sliderDetail.clipToPadding = false
        binding.sliderDetail.clipChildren = false
        binding.sliderDetail.offscreenPageLimit = 3

        val internalRecyclerView = binding.sliderDetail.getChildAt(0) as? RecyclerView
        internalRecyclerView?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        if (initialSliderItems.size > 1) {
            binding.dotIndicator.visibility = View.VISIBLE
            binding.dotIndicator.attachTo(binding.sliderDetail)
        } else {
            binding.dotIndicator.visibility = View.GONE
        }
    }

    private fun initLists() {
        if (item.size.isNotEmpty()) {
//            binding.textViewSizeLabel.visibility = View.VISIBLE
            binding.textView17.visibility = View.VISIBLE
            binding.sizeList.visibility = View.VISIBLE
            binding.sizeList.adapter = SizeAdapter(item.size)
            binding.sizeList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            binding.textView17.visibility = View.GONE
            binding.sizeList.visibility = View.GONE
        }

        if (item.colorResourceNames.isNotEmpty()) {
            binding.colorList.visibility = View.VISIBLE
            colorAdapter = ColorAdapter(item.colorResourceNames)
            binding.colorList.adapter = colorAdapter
            binding.colorList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        } else {
            binding.colorList.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }
}
