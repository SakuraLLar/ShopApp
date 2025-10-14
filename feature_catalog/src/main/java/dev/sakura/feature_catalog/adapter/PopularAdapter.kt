package dev.sakura.feature_catalog.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import dev.sakura.common_ui.R
import dev.sakura.core.favourites.FavouritesManager
import dev.sakura.feature_catalog.activity.DetailActivity
import dev.sakura.feature_catalog.databinding.ViewHolderRecommendedBinding
import dev.sakura.models.ItemsModel

class PopularAdapter(
    private val favouritesManager: FavouritesManager,
) : ListAdapter<ItemsModel, PopularAdapter.ViewHolder>(PopularDiffCallback()) {

    private var favouritesStatusMap: Map<Int, Boolean> = emptyMap()

    fun setFavouritesStatusMap(newMap: Map<Int, Boolean>) {
        val oldMap = favouritesStatusMap
        favouritesStatusMap = newMap
        currentList.forEachIndexed { index, item ->
            val oldStatus = oldMap[item.resourceId] ?: false
            val newStatus = newMap[item.resourceId] ?: false
            if (oldStatus != newStatus) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ViewHolderRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, favouritesManager)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val currentItem = getItem(position)
        val isFavourite = favouritesStatusMap[currentItem.resourceId] ?: false
        holder.bind(currentItem, isFavourite)
    }

    class ViewHolder(
        val binding: ViewHolderRecommendedBinding,
        private val favouritesManager: FavouritesManager,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemsModel, isFavourite: Boolean) {
            binding.txtTitle.text = item.title
            binding.txtPrice.text = String.format("$%.2f", item.price)
            binding.txtRating.text = item.rating.toString()

            Glide.with(itemView.context)
                .load(item.resourceId)
                .transform(CenterCrop())
                .into(binding.pic)

            updateIconBasedOnStatus(isFavourite)

            binding.imageView7.setOnClickListener {
                favouritesManager.toggleFavouriteStatus(item)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("object", item)
                itemView.context.startActivity(intent)
            }
        }

        private fun updateIconBasedOnStatus(isFavourite: Boolean) {
            val context = itemView.context
            if (isFavourite) {
                binding.imageView7.setColorFilter(ContextCompat.getColor(context, R.color.red))
            } else {
                binding.imageView7.setColorFilter(ContextCompat.getColor(context, R.color.grey))
            }
        }
    }

    class PopularDiffCallback : DiffUtil.ItemCallback<ItemsModel>() {
        override fun areItemsTheSame(oldItem: ItemsModel, newItem: ItemsModel): Boolean {
            return oldItem.resourceId == newItem.resourceId
        }

        override fun areContentsTheSame(oldItem: ItemsModel, newItem: ItemsModel): Boolean {
            return oldItem == newItem
        }
    }
}
