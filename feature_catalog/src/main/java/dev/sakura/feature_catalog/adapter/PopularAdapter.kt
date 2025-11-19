package dev.sakura.feature_catalog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.common_ui.R
import dev.sakura.feature_catalog.databinding.ViewHolderPopularBinding
import dev.sakura.models.ItemsModel

data class PopularItem(
    val model: ItemsModel,
    val isFavourite: Boolean,
)

class PopularAdapter(
    private val onItemClick: (ItemsModel) -> Unit,
    private val onFavouriteClick: (ItemsModel) -> Unit,
) : ListAdapter<PopularItem, PopularAdapter.ViewHolder>(PopularDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ViewHolderPopularBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick, onFavouriteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ViewHolderPopularBinding,
        private val onItemClick: (ItemsModel) -> Unit,
        private val onFavouriteClick: (ItemsModel) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PopularItem) {
            binding.txtTitlePopular.text = item.model.title
            binding.txtPricePopular.text = String.format("$%.2f", item.model.price)
            binding.txtRatingPopular.text = item.model.rating.toString()

            Glide.with(itemView.context)
                .load(item.model.resourceId)
                .into(binding.imgPicPopular)

            val context = itemView.context
            val favouriteColor = ContextCompat.getColor(context, R.color.red)
            val defaultColor = ContextCompat.getColor(context, android.R.color.white)

            if (item.isFavourite) {
                binding.imgAddToFavourite.setColorFilter(favouriteColor)
            } else {
                binding.imgAddToFavourite.setColorFilter(defaultColor)
            }

            binding.imgAddToFavourite.setOnClickListener {
                onFavouriteClick(item.model)
            }
            itemView.setOnClickListener {
                onItemClick(item.model)
            }
        }
    }

    class PopularDiffCallback : DiffUtil.ItemCallback<PopularItem>() {
        override fun areItemsTheSame(oldItem: PopularItem, newItem: PopularItem): Boolean {
            return oldItem.model.resourceId == newItem.model.resourceId
        }

        override fun areContentsTheSame(oldItem: PopularItem, newItem: PopularItem): Boolean {
            return oldItem == newItem
        }
    }
}
