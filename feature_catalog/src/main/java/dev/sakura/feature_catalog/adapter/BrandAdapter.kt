package dev.sakura.feature_catalog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import dev.sakura.common_ui.R as CommonR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.feature_catalog.databinding.ViewHolderBrandBinding
import dev.sakura.models.BrandModel

class BrandAdapter(
    private val onBrandClick: (BrandModel) -> Unit,
) : ListAdapter<BrandAdapter.BrandItem, BrandAdapter.ViewHolder>(BrandDiffCallback()) {

    data class BrandItem(val brand: BrandModel, val isSelected: Boolean)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ViewHolderBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onBrandClick)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ViewHolderBrandBinding,
        private val onBrandClick: (BrandModel) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BrandItem) {
            val context = itemView.context
            binding.txtTitleBrand.text = item.brand.title
            Glide.with(context)
                .load(item.brand.resourceId)
                .into(binding.imgPicBrand)

            if (item.isSelected) {
                binding.txtTitleBrand.visibility = View.VISIBLE

                val activeColor = ContextCompat.getColor(context, CommonR.color.yellow)
                val onActiveColor = ContextCompat.getColor(context, CommonR.color.black)

                binding.brandCardView.setCardBackgroundColor(activeColor)
                binding.txtTitleBrand.setTextColor(onActiveColor)
                binding.imgPicBrand.setColorFilter(onActiveColor)
                binding.brandCardView.strokeWidth = 0

            } else {
                binding.txtTitleBrand.visibility = View.GONE

                val defaultBackgroundColor = ContextCompat.getColor(context, android.R.color.transparent)
                val onDefaultColor = ContextCompat.getColor(context, CommonR.color.grey)
                val strokeColor = ContextCompat.getColor(context, CommonR.color.grey)

                binding.brandCardView.setCardBackgroundColor(defaultBackgroundColor)
                binding.imgPicBrand.setColorFilter(onDefaultColor)
                binding.brandCardView.setStrokeColor(strokeColor)
                binding.brandCardView.setStrokeWidth(1.dpToPx())
            }
            binding.root.setOnClickListener { onBrandClick(item.brand) }
        }

        private fun Int.dpToPx(): Int {
            val density = android.content.res.Resources.getSystem().displayMetrics.density
            return (this * density).toInt()
        }
    }

    class BrandDiffCallback : DiffUtil.ItemCallback<BrandItem>() {
        override fun areItemsTheSame(oldItem: BrandItem, newItem: BrandItem): Boolean {
            return oldItem.brand.resourceId == newItem.brand.resourceId
        }

        override fun areContentsTheSame(oldItem: BrandItem, newItem: BrandItem): Boolean {
            return oldItem == newItem
        }
    }
}
