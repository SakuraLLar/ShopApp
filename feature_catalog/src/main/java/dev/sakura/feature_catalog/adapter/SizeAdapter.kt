package dev.sakura.feature_catalog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import dev.sakura.common_ui.R
import dev.sakura.feature_catalog.databinding.ViewHolderSizeBinding

class SizeAdapter(private var items: MutableList<String>) :
    RecyclerView.Adapter<SizeAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION
    private lateinit var context: Context

    fun updateData(newItems: MutableList<String>) {
        items = newItems
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        context = parent.context
        val binding = ViewHolderSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.binding.txtSize.text = items[position]

        holder.binding.root.setOnClickListener {
            val previousSelectedPosition = selectedPosition

            if (selectedPosition == holder.adapterPosition) {
                selectedPosition = RecyclerView.NO_POSITION
                notifyItemChanged(previousSelectedPosition)
            } else {
                selectedPosition = holder.adapterPosition

                if (previousSelectedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousSelectedPosition)
                }
                notifyItemChanged(selectedPosition)
            }
        }

        if (selectedPosition == position) {
            holder.binding.sizeItemRoot.setBackgroundResource(R.drawable.size_item_bg_selected)
            holder.binding.txtSize.setTextColor(ContextCompat.getColor(context, R.color.primary))
        } else {
            holder.binding.sizeItemRoot.setBackgroundResource(R.drawable.size_item_bg_default)
            holder.binding.txtSize.setTextColor(ContextCompat.getColor(context, R.color.onSurfaceVariant))
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewHolderSizeBinding) :
        RecyclerView.ViewHolder(binding.root)
}
