package dev.sakura.feature_catalog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dev.sakura.feature_catalog.R
import dev.sakura.models.SliderModel

class SliderAdapter(
    private var sliderItemsInternal: MutableList<SliderModel>,
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private lateinit var context: Context
    private val runnable = Runnable {
        notifyDataSetChanged()
    }

    fun updateItems(newItems: List<SliderModel>) {
        sliderItemsInternal.clear()
        sliderItemsInternal.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SliderViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.slider_item_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SliderViewHolder,
        position: Int,
    ) {
        holder.setImage(sliderItemsInternal[position], context)
    }

    override fun getItemCount(): Int = sliderItemsInternal.size

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)

        fun setImage(sliderItem: SliderModel, context: Context) {
            Glide.with(context)
                .load(sliderItem.resourceId)
                .into(imageView)
        }
    }
}
