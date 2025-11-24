package dev.sakura.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class NotificationModel(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Date,
    val isRead: Boolean = false,
) : Parcelable
