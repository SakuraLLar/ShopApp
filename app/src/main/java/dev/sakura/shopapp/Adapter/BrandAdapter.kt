package dev.sakura.shopapp.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.sakura.shopapp.Model.BrandModel
import dev.sakura.shopapp.R
import dev.sakura.shopapp.databinding.ViewHolderBrandBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BrandAdapter(private var items: MutableList<BrandModel>) :
    RecyclerView.Adapter<BrandAdapter.ViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    private lateinit var context: Context

    private val adapterScope = CoroutineScope(Dispatchers.Main)
    private var deselectJob: Job? = null
    private val AUTO_DESELECT_DELAY_MS = 10000L

    fun updateData(newItems: MutableList<BrandModel>) {
        items = newItems
        selectedPosition = -1
        lastSelectedPosition = -1
        cancelAutoDeselect()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        context = parent.context
        val binding = ViewHolderBrandBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val item = items[position]

        holder.binding.txtTitle.text = item.title

        Glide.with(holder.itemView.context)
            .load(item.resourceId)
            .into(holder.binding.pic)

        holder.binding.root.setOnClickListener {
            val currentAdapterPosition = holder.adapterPosition
            if (currentAdapterPosition != RecyclerView.NO_POSITION) {
                cancelAutoDeselect()

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
                        startAutoDeselect()
                    }
                }
            }
        }

        if (selectedPosition == position) {
            holder.binding.pic.setBackgroundColor(0)
            holder.binding.mainLayout.setBackgroundResource(R.drawable.button_yellow_bg)
            ImageViewCompat.setImageTintList(
                holder.binding.pic,
                ColorStateList.valueOf(context.getColor(R.color.white))

            )
            holder.binding.txtTitle.visibility = View.VISIBLE
            holder.binding.txtTitle.setTextColor(context.resources.getColor(R.color.white))
        } else {
            holder.binding.pic.setBackgroundResource(R.drawable.yellow_bg)
            holder.binding.mainLayout.setBackgroundResource(0)
            ImageViewCompat.setImageTintList(
                holder.binding.pic,
                ColorStateList.valueOf(context.getColor(R.color.black))
            )
            holder.binding.txtTitle.visibility = View.GONE
        }
    }

    private fun startAutoDeselect() {
        deselectJob?.cancel()
        deselectJob = adapterScope.launch {
            delay(AUTO_DESELECT_DELAY_MS)

            if (selectedPosition != RecyclerView.NO_POSITION) {
                val positionToDeselect = selectedPosition
                selectedPosition = -1
                notifyItemChanged(positionToDeselect)
            }
        }
    }

    private fun cancelAutoDeselect() {
        deselectJob?.cancel()
        deselectJob = null
    }

    fun cleanupCoroutines() {
        adapterScope.coroutineContext.cancelChildren()
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: ViewHolderBrandBinding) :
        RecyclerView.ViewHolder(binding.root)
}
