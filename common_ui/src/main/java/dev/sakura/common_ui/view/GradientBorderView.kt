package dev.sakura.common_ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import dev.sakura.common_ui.R

class GradientBorderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var strokeWidthPx: Float = 10f
    private var startColor: Int = Color.MAGENTA
    private var centerColor: Int = Color.CYAN
    private var endColor: Int = Color.YELLOW

    init {
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.GradientBorderView, 0, 0)

            strokeWidthPx =
                typedArray.getDimension(R.styleable.GradientBorderView_gbv_strokeWidth, 10f)
            startColor =
                typedArray.getColor(R.styleable.GradientBorderView_gbv_startColor, Color.MAGENTA)
            centerColor =
                typedArray.getColor(R.styleable.GradientBorderView_gbv_centerColor, Color.CYAN)
            endColor =
                typedArray.getColor(R.styleable.GradientBorderView_gbv_endColor, Color.YELLOW)
            typedArray.recycle()
        }

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidthPx
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val gradient = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(startColor, centerColor, endColor),
            null,
            Shader.TileMode.CLAMP
        )

        paint.shader = gradient

        val halfStroke = strokeWidthPx / 2
        canvas.drawOval(
            halfStroke,
            halfStroke,
            width - halfStroke,
            height - halfStroke,
            paint
        )
    }

    fun setGradientColors(start: Int, center: Int, end: Int) {
        startColor = ContextCompat.getColor(context, start)
        centerColor = ContextCompat.getColor(context, center)
        endColor = ContextCompat.getColor(context, end)
        invalidate()
    }
}
