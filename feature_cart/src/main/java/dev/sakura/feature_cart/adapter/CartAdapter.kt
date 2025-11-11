package dev.sakura.feature_cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sakura.feature_cart.databinding.ItemCartBinding
import dev.sakura.models.CartItemModel

class CartAdapter(
    private val onIncreaseQuantity: (CartItemModel) -> Unit,
    private val onDecreaseQuantity: (CartItemModel) -> Unit,
    private val onRemoveItem: (CartItemModel) -> Unit,
) : ListAdapter<CartItemModel, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItemModel) {
            binding.apply {
                textViewProductTitleCart.text = cartItem.title
                textViewProductPriceCart.text =
                    String.format("$%.2f", cartItem.price * cartItem.quantity)
                textViewQuantityCart.text = cartItem.quantity.toString()

                val imageId = cartItem.imageResourceId
                if (imageId != null && imageId != 0) {
                    imgViewProductCart.setImageResource(imageId)
                }

                buttonIncreaseQuantity.setOnClickListener {
                    onIncreaseQuantity(cartItem)
                }
                buttonDecreaseQuantity.setOnClickListener {
                    onDecreaseQuantity(cartItem)
                }
                buttonRemoveItemCart.setOnClickListener {
                    onRemoveItem(cartItem)
                }
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItemModel>() {
        override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem == newItem
        }
    }
}
