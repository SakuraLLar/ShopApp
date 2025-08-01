package dev.sakura.shopapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import dev.sakura.shopapp.model.SliderModel
import dev.sakura.shopapp.R

class SliderAdapter(
    private var sliderItemsInternal: MutableList<SliderModel>,
    private val viewPager2: ViewPager2,
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
        if (position == sliderItemsInternal.lastIndex - 1 && sliderItemsInternal.size > 1) {
            viewPager2.postDelayed(runnable, 3000)
        }
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
