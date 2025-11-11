package dev.sakura.feature_profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import dev.sakura.feature_profile.databinding.ItemCoverChoiceBinding

data class Cover(
    val id: Int,
    val drawableRes: Int,
)

class CoverAdapter(
    private val covers: List<Cover>,
    private val onCoverSelected: (Cover) -> Unit,
) : RecyclerView.Adapter<CoverAdapter.CoverViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    fun setInitialSelection(selectedCoverId: Int) {
        val index = covers.indexOfFirst { it.id == selectedCoverId }

        if (index != -1) {
            selectedPosition = index
            notifyItemChanged(selectedPosition)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CoverViewHolder {
        val binding = ItemCoverChoiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoverViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        val cover = covers[position]

        holder.bind(cover, position == selectedPosition)
        holder.itemView.setOnClickListener {
            if (selectedPosition != holder.adapterPosition) {
                val oldSelectedPosition = selectedPosition

                if (oldSelectedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldSelectedPosition)
                }

                selectedPosition = holder.adapterPosition
                notifyItemChanged(selectedPosition)
                onCoverSelected(cover)
            }
        }
    }

    override fun getItemCount(): Int = covers.size

    class CoverViewHolder(private val binding: ItemCoverChoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val context: Context = itemView.context

        fun bind(cover: Cover, isSelected: Boolean) {
            binding.imgCoverPreview.setImageDrawable(
                ContextCompat.getDrawable(context, cover.drawableRes)
            )

            val card = itemView as MaterialCardView

            if (isSelected) {
                card.strokeWidth = (3 * context.resources.displayMetrics.density).toInt()
            } else {
                card.strokeWidth = 0
            }
        }
    }
}
