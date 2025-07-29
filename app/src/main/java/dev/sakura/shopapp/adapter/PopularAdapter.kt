package dev.sakura.shopapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import dev.sakura.shopapp.activity.DetailActivity
import dev.sakura.shopapp.databinding.ViewHolderRecommendedBinding
import dev.sakura.shopapp.model.ItemsModel

class PopularAdapter(private val items: MutableList<ItemsModel>) :
    RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    private var context: Context? = null

    fun updateDataWith(newPopularList: List<ItemsModel>) {
        val diffCallback = PopularDiffCallback(this.items, newPopularList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(newPopularList)
        diffResult.dispatchUpdatesTo(this)
    }

//    fun updateData(newPopularList: MutableList<ItemsModel>) {
//        items.clear()
//        items.addAll(newPopularList)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): PopularAdapter.ViewHolder {
        context = parent.context
        val binding =
            ViewHolderRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PopularAdapter.ViewHolder,
        position: Int,
    ) {
        val currentItem = items[position]

        holder.binding.txtTitle.text = currentItem.title
        holder.binding.txtPrice.text = "$" + currentItem.price.toString()
        holder.binding.txtRating.text = currentItem.rating.toString()

        val requestOptions = RequestOptions().transform(CenterCrop())

        Glide.with(holder.itemView.context)
            .load(currentItem.resourceId)
            .apply(requestOptions)
            .into(holder.binding.pic)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("object", items[position])
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ViewHolderRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root)

    class PopularDiffCallback(
        private val oldList: List<ItemsModel>,
        private val newList: List<ItemsModel>,
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int)
        : Boolean {
            return oldList[oldItemPosition].title == newList[newItemPosition].title &&
                    oldList[oldItemPosition].resourceId == newList[newItemPosition].resourceId
        }

        override fun areContentsTheSame(
            oldItemPosition: Int,
            newItemPosition: Int,
        ): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
