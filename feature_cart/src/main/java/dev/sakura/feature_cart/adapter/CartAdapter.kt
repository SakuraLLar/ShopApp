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
    private val onToggleSelection: (productId: String) -> Unit,
) : ListAdapter<CartItemModel, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int,
        payloads: MutableList<Any>,
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            if (payloads.contains("PAYLOAD_CHECK_CHANGED")) {
                holder.updateCheckbox(getItem(position))
            }
        }
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(
            cartItem = currentItem,
            onIncreaseQuantity = onIncreaseQuantity,
            onDecreaseQuantity = onDecreaseQuantity,
            onRemoveItem = onRemoveItem,
            onToggleSelection = onToggleSelection
        )
    }

    class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            cartItem: CartItemModel,
            onIncreaseQuantity: (CartItemModel) -> Unit,
            onDecreaseQuantity: (CartItemModel) -> Unit,
            onRemoveItem: (CartItemModel) -> Unit,
            onToggleSelection: (productId: String) -> Unit,
        ) {
            binding.apply {
                updateCheckbox(cartItem)

                textViewProductTitleCart.text = cartItem.title
                textViewProductPriceCart.text =
                    String.format("$%.2f", cartItem.price * cartItem.quantity)
                textViewQuantityCart.text = cartItem.quantity.toString()

                val imageId = cartItem.imageResourceId
                if (imageId != null && imageId != 0) {
                    imgViewProductCart.setImageResource(imageId)
                }

                checkboxItemCart.setOnClickListener {
                    onToggleSelection(cartItem.productId)
                }

                buttonIncreaseQuantity.setOnClickListener { onIncreaseQuantity(cartItem) }
                buttonDecreaseQuantity.setOnClickListener { onDecreaseQuantity(cartItem) }
                buttonRemoveItemCart.setOnClickListener { onRemoveItem(cartItem) }
            }
        }

        fun updateCheckbox(cartItem: CartItemModel) {
            binding.checkboxItemCart.isChecked = cartItem.isSelected
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItemModel>() {
        override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: CartItemModel, newItem: CartItemModel): Any? {
            if (oldItem.isSelected != newItem.isSelected) {
                return "PAYLOAD_CHECK_CHANGED"
            }
            return super.getChangePayload(oldItem, newItem)
        }
    }
}
