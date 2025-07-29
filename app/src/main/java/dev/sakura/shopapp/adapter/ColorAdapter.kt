package dev.sakura.shopapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.shopapp.R
import dev.sakura.shopapp.databinding.ViewholderColorBinding


class ColorAdapter(private var items: List<String>) :
    RecyclerView.Adapter<ColorAdapter.ViewHolder>() {
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
        val binding = ViewholderColorBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val imageName = items[position]
        val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

        if (resourceId != 0) {
            Glide.with(holder.itemView.context)
                .load(resourceId)
                .into(holder.binding.pic)
        } else {
            holder.binding.pic.setImageDrawable(
                ContextCompat.getColor(
                    context,
                    R.color.lightGrey
                ).toDrawable()
            )
        }

        holder.binding.root.setOnClickListener {
            val currentAdapterPosition = holder.adapterPosition
            if (currentAdapterPosition != RecyclerView.NO_POSITION) {

                if (selectedPosition == currentAdapterPosition) {
                    selectedPosition = -1
                    notifyItemChanged(currentAdapterPosition)
                } else {
                    val previouslySelectedPosition = selectedPosition
                    selectedPosition = currentAdapterPosition

                    if (previouslySelectedPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(previouslySelectedPosition)
                    }
                    notifyItemChanged(selectedPosition)
                }
            }
        }

        if (selectedPosition == position) {
            holder.binding.layoutColor.setBackgroundResource(R.drawable.yellow_bg)
        } else {
            holder.binding.layoutColor.setBackgroundResource(R.drawable.grey_bg_selected)
        }
    }

    override fun getItemCount(): Int {
        val count = items.size
        return count
    }

    inner class ViewHolder(val binding: ViewholderColorBinding) :
        RecyclerView.ViewHolder(binding.root)
}
