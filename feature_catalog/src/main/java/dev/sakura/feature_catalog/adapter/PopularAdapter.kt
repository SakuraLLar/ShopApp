package dev.sakura.shopapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import dev.sakura.shopapp.R
import dev.sakura.shopapp.activity.DetailActivity
import dev.sakura.shopapp.databinding.ViewHolderRecommendedBinding
import dev.sakura.shopapp.model.ItemsModel
import dev.sakura.shopapp.viewModel.FavouritesViewModel

class PopularAdapter(
    private val items: MutableList<ItemsModel>,
    private val favouritesViewModel: FavouritesViewModel,
    private val lifecycleOwner: LifecycleOwner,
) : RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    fun updateDataWith(newPopularList: List<ItemsModel>) {
        val diffCallback = PopularDiffCallback(this.items, newPopularList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(newPopularList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding =
            ViewHolderRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, favouritesViewModel, lifecycleOwner)
    }

    override fun onBindViewHolder(
        holder: PopularAdapter.ViewHolder,
        position: Int,
    ) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = items.size

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.onRecycled()
    }

    class ViewHolder(
        val binding: ViewHolderRecommendedBinding,
        private val favouritesVm: FavouritesViewModel,
        private val lifecycleOwner: LifecycleOwner,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentItemBound: ItemsModel? = null
        private var favouriteStatusObserver: Observer<Map<Int, Boolean>>? = null

        fun bind(currentItem: ItemsModel) {
            currentItemBound = currentItem
            binding.txtTitle.text = currentItem.title
            binding.txtPrice.text = String.format("$%.2f", currentItem.price)
            binding.txtRating.text = currentItem.rating.toString()

            val requestOptions = RequestOptions().transform(CenterCrop())

            Glide.with(itemView.context)
                .load(currentItem.resourceId)
                .apply(requestOptions)
                .into(binding.pic)

            updateIconBasedOnStatus(
                favouritesVm.favouriteStatusMap.value?.get(currentItem.resourceId) ?: false
            )

            favouriteStatusObserver?.let {
                favouritesVm.favouriteStatusMap.removeObserver(it)

            }

            favouriteStatusObserver = Observer { statusMap ->
                currentItemBound?.let { boundItem ->
                    if (currentItem.resourceId == boundItem.resourceId) {
                        val isFavCurrent = statusMap[boundItem.resourceId] ?: false
                        updateIconBasedOnStatus(isFavCurrent)
                    }
                }
            }

            favouritesVm.favouriteStatusMap.observe(lifecycleOwner, favouriteStatusObserver!!)

            binding.imageView7.setOnClickListener {
                currentItemBound?.let { boundItem ->
                    favouritesVm.toggleFavouriteStatus(boundItem)
                }
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("object", currentItem)
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

        fun onRecycled() {
            favouriteStatusObserver?.let {
                favouritesVm.favouriteStatusMap.removeObserver(it)
            }
            favouriteStatusObserver = null
            currentItemBound = null
        }
    }
}

class PopularDiffCallback(
    private val oldList: List<ItemsModel>,
    private val newList: List<ItemsModel>,
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition].resourceId == newList[newItemPosition].resourceId &&
                oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int,
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
