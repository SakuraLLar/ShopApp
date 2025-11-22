package dev.sakura.feature_orders.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration(
    private val verticalSpaceHeight: Int,
    private val topSpaceHeight: Int? = null,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val position = parent.getChildAdapterPosition(view)

        if (position == 0 && topSpaceHeight != null) {
            outRect.top = topSpaceHeight
        }

        if (position != 0) {
            if (position == 1 && topSpaceHeight != null) {
                outRect.top = verticalSpaceHeight
            } else if (position > 0) {
                outRect.top = verticalSpaceHeight
            }
        }
    }
}
