package dev.sakura.common_ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.sakura.common_ui.databinding.ItemNotificationBinding
import dev.sakura.models.NotificationModel
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    private val onItemClick: (NotificationModel) -> Unit,
) : ListAdapter<NotificationModel, NotificationAdapter.NotificationViewHolder>(
    NotificationDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotificationViewHolder(
        private val binding: ItemNotificationBinding,
        private val onItemClick: (NotificationModel) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val timeFormatter = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault())

        fun bind(notification: NotificationModel) {
            binding.apply {
                txtNotificationTitle.text = notification.title
                txtNotificationMessage.text = notification.message
                txtNotificationTimestamp.text = timeFormatter.format(notification.timestamp)
                imgUnreadIndicator.visibility = if (notification.isRead) View.GONE else View.VISIBLE

                root.setOnClickListener {
                    onItemClick(notification)
                }
            }
        }
    }

    class NotificationDiffCallback : DiffUtil.ItemCallback<NotificationModel>() {
        override fun areItemsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel,
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
