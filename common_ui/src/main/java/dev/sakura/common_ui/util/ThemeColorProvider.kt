package dev.sakura.common_ui.util

import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import com.google.android.material.color.MaterialColors
import dev.sakura.common_ui.R

object ThemeColorProvider {
    @ColorInt
    fun getSurfaceColor(view: View): Int {
        return MaterialColors.getColor(view, R.attr.myColorSurface, Color.WHITE)
    }
}
