package dev.sakura.feature_orders.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.feature_orders.databinding.ItemOrdersBinding
import dev.sakura.models.ItemsModel

class OrdersAdapter(
    private val onItemClick: (ItemsModel) -> Unit,
) : ListAdapter<ItemsModel, OrdersAdapter.OrderViewHolder>(OrdersDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrdersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class OrderViewHolder(private val binding: ItemOrdersBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: ItemsModel) {
            binding.textViewItemTitle.text = item.title
            binding.textViewItemPrice.text = "Цена: ${item.price}$"
            binding.textViewItemQuantity.text = "Количество: ${item.numberInCart}"
            binding.textViewOrderStatus.text = "Товар оформляется"

            Glide.with(itemView.context)
                .load(item.resourceId)
                .into(binding.imageViewItemPic)

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object OrdersDiffCallback : DiffUtil.ItemCallback<ItemsModel>() {
        override fun areItemsTheSame(oldItem: ItemsModel, newItem: ItemsModel): Boolean {
            return oldItem.resourceId == newItem.resourceId
        }

        override fun areContentsTheSame(oldItem: ItemsModel, newItem: ItemsModel): Boolean {
            return oldItem == newItem
        }
    }
}
