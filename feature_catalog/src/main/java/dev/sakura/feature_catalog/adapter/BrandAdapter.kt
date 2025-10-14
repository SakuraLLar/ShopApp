package dev.sakura.feature_catalog.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.common_ui.R
import dev.sakura.feature_catalog.databinding.ViewHolderBrandBinding
import dev.sakura.models.BrandModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BrandAdapter(
    private val onBrandClick: (BrandModel) -> Unit,
) : ListAdapter<BrandAdapter.BrandItem, BrandAdapter.ViewHolder>(BrandDiffCallback()) {

    data class BrandItem(val brand: BrandModel, val isSelected: Boolean)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ViewHolderBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            binding.txtTitle.text = item.brand.title

            Glide.with(itemView.context)
                .load(item.brand.resourceId)
                .into(binding.pic)

            binding.root.setOnClickListener {
                onBrandClick(item.brand)
            }

            val context = itemView.context
            if (item.isSelected) {
                binding.pic.setBackgroundColor(0)
                binding.mainLayout.setBackgroundResource(R.drawable.button_yellow_bg)
                ImageViewCompat.setImageTintList(
                    binding.pic,
                    ColorStateList.valueOf(context.getColor(R.color.white))
                )
                binding.txtTitle.visibility = View.VISIBLE
                binding.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.pic.setBackgroundColor(R.drawable.yellow_bg)
                binding.mainLayout.setBackgroundResource(0)
                ImageViewCompat.setImageTintList(
                    binding.pic,
                    ColorStateList.valueOf(context.getColor(R.color.black))
                )
                binding.txtTitle.visibility = View.GONE
            }
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
