package dev.sakura.feature_catalog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            binding.root.isActivated = item.isSelected
            binding.txtTitle.visibility = if (item.isSelected) View.VISIBLE else View.GONE
            binding.root.setOnClickListener { onBrandClick(item.brand) }
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
