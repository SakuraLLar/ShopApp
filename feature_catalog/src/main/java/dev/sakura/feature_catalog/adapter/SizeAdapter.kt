package dev.sakura.feature_catalog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.sakura.common_ui.R
import dev.sakura.feature_catalog.databinding.ViewholderSizeBinding

class SizeAdapter(private var items: MutableList<String>) :
    RecyclerView.Adapter<SizeAdapter.ViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    private lateinit var context: Context

    fun updateData(newItems: MutableList<String>) {
        items = newItems
        selectedPosition = -1
        lastSelectedPosition = -1
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        context = parent.context
        val binding = ViewholderSizeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        holder.binding.txtSize.text = items[position]

        holder.binding.root.setOnClickListener {
            val currentAdapterPosition = holder.adapterPosition
            if (currentAdapterPosition != RecyclerView.NO_POSITION) {

                if (selectedPosition == currentAdapterPosition) {
                    selectedPosition = -1
                    notifyItemChanged(currentAdapterPosition)
                } else {
                    lastSelectedPosition = selectedPosition
                    selectedPosition = currentAdapterPosition

                    if (lastSelectedPosition != RecyclerView.NO_POSITION && lastSelectedPosition < items.size && lastSelectedPosition >= 0) {
                        notifyItemChanged(lastSelectedPosition)
                    }

                    if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < items.size) {
                        notifyItemChanged(selectedPosition)
                    }
                }
            }
        }

        if (selectedPosition == position) {
            holder.binding.layoutColor.setBackgroundResource(R.drawable.yellow_bg)
            holder.binding.txtSize.setTextColor(context.resources.getColor(R.color.black))
        } else {
            holder.binding.layoutColor.setBackgroundResource(R.drawable.grey_bg_selected)
            holder.binding.txtSize.setTextColor(context.resources.getColor(R.color.black))
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ViewholderSizeBinding) :
        RecyclerView.ViewHolder(binding.root)
}
