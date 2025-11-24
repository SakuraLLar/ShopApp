package dev.sakura.common_ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.sakura.common_ui.adapter.NotificationAdapter
import dev.sakura.common_ui.databinding.FragmentNotificationsDialogBinding
import dev.sakura.models.NotificationModel
import java.util.Date

class NotificationsDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentNotificationsDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNotificationsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter { notification ->
            Toast.makeText(context, "Clicked: ${notification.title}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewNotifications.adapter = notificationAdapter
    }

    private fun loadDummyData() {
        val dummyList = listOf(
            NotificationModel(
                "1",
                "Скидка 50% на всё!",
                "Только сегодня у вас есть шанс купить товары по суперцене.",
                Date(System.currentTimeMillis() - 1000 * 60 * 5),
                isRead = false
            ),
            NotificationModel(
                "2", "Ваш заказ в пути", "Заказ #12345 был передан в службу доставки.",
                Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2), isRead = false
            ),
            NotificationModel(
                "3", "Новая коллекция", "Оцените новые поступления в разделе 'Одежда'.",
                Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24), isRead = true
            ),
            NotificationModel(
                "4", "Добро пожаловать!", "Спасибо, что присоединились к нашему приложению.",
                Date(System.currentTimeMillis() - 1000 * 60 * 60 * 48), isRead = true
            )
        )
        notificationAdapter.submitList(dummyList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NotificationsDialog"

        fun newInstance(): NotificationsDialogFragment {
            return NotificationsDialogFragment()
        }
    }
}
