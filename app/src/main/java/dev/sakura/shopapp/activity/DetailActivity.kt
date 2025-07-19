package dev.sakura.shopapp.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.sakura.shopapp.Adapter.ColorAdapter
import dev.sakura.shopapp.Adapter.SizeAdapter
import dev.sakura.shopapp.Adapter.SliderAdapter
import dev.sakura.shopapp.Helper.ManagmentCart
import dev.sakura.shopapp.Model.ItemsModel
import dev.sakura.shopapp.Model.SliderModel
import dev.sakura.shopapp.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemsModel
    private var numberOder = 1
    private lateinit var managmentCart: ManagmentCart
    private lateinit var colorAdapter: ColorAdapter
    private lateinit var sliderAdapter: SliderAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managmentCart = ManagmentCart(this)

        getBundle()
        setupSlider()
        initLists()
    }

    private fun getBundle() {
        val receivedItem: ItemsModel? = intent.getParcelableExtra("object")

        if (receivedItem == null) {
            return
        }

        item = receivedItem

        binding.txtTitleDetail.text = item.title
        binding.txtDescription.text = item.description
        binding.txtPriceDetail.text = "$" + item.price
        binding.txtRatingDetail.text = "${item.rating} Rating"
        binding.btnAddToCart.setOnClickListener {
            item.numberInCart = numberOder
            managmentCart.insertFood(item)
        }
        binding.btnBack.setOnClickListener { finish() }
        binding.btnCart.setOnClickListener {

        }
    }

    private fun setupSlider() {
        val initialSliderItems = ArrayList<SliderModel>()

        if (item.resourceId != 0) {
            initialSliderItems.add(SliderModel(item.resourceId))
        }

        sliderAdapter = SliderAdapter(initialSliderItems, binding.sliderDetail)
        binding.sliderDetail.adapter = SliderAdapter(initialSliderItems, binding.sliderDetail)
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
            colorAdapter = ColorAdapter(item.colorResourceNames)
            binding.colorList.adapter = colorAdapter
            binding.colorList.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


            binding.colorList.requestLayout()
            binding.colorList.visibility = View.VISIBLE

        } else {
            binding.colorList.visibility = View.GONE
        }
    }
}
