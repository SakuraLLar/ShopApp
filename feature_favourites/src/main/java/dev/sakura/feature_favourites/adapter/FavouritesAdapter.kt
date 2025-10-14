package dev.sakura.feature_favourites.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.feature_favourites.databinding.ItemFavouritesBinding
import dev.sakura.models.ItemsModel

class FavouritesAdapter(
    private val onItemClicked: (ItemsModel) -> Unit,
    private val onRemoveFromFavouritesClicked: (ItemsModel) -> Unit,
) : ListAdapter<ItemsModel, FavouritesAdapter.FavouritesViewHolder>(FavouritesDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val binding =
            ItemFavouritesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouritesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class FavouritesViewHolder(private val binding: ItemFavouritesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ItemsModel) {
            binding.txtTitleFavouritesItem.text = item.title
            binding.txtPriceFavouritesItem.text = String.format("$%.2f", item.price)

            Glide.with(itemView.context)
                .load(item.resourceId)
                .into(binding.picFavouritesItem)

            binding.root.setOnClickListener {
                onItemClicked(item)
            }

            binding.btnRemoveFromFavourites.setOnClickListener {
                onRemoveFromFavouritesClicked(item)
            }
        }
    }

    companion object FavouritesDiffCallback : DiffUtil.ItemCallback<ItemsModel>() {
        override fun areItemsTheSame(oldItem: ItemsModel, newItem: ItemsModel): Boolean {
            return oldItem.resourceId == newItem.resourceId
        }

        override fun areContentsTheSame(oldItem: ItemsModel, newItem: ItemsModel): Boolean {
            return oldItem == newItem
        }
    }
}
