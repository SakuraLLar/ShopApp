package dev.sakura.feature_cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sakura.models.CartItem
import dev.sakura.feature_cart.databinding.ItemCartBinding

class CartAdapter(
    private val onIncreaseQuantity: (CartItem) -> Unit,
    private val onDecreaseQuantity: (CartItem) -> Unit,
    private val onRemoveItem: (CartItem) -> Unit,
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

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
        fun bind(cartItem: CartItem) {
            binding.apply {
                textViewProductTitleCart.text = cartItem.title
                textViewProductPriceCart.text =
                    String.format("$%.2f", cartItem.price * cartItem.quantity)
                textViewQuantityCart.text = cartItem.quantity.toString()

                val imageId = cartItem.imageResourcedId
                if (imageId != null && imageId != 0) {
                    imageViewProductCart.setImageResource(imageId)
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

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}
