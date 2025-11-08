package dev.sakura.feature_catalog.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.common_ui.R
import dev.sakura.feature_catalog.databinding.ViewholderColorBinding

class ColorAdapter(private var items: List<String>) :
    RecyclerView.Adapter<ColorAdapter.ViewHolder>() {

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
                ContextCompat.getColor(context, R.color.lightGrey).toDrawable()
            )
        }

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
            holder.binding.viewOutline.visibility = View.VISIBLE
        } else {
            holder.binding.viewOutline.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        val count = items.size
        return count
    }

    inner class ViewHolder(val binding: ViewholderColorBinding) :
        RecyclerView.ViewHolder(binding.root)
}
