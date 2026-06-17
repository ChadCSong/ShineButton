package com.sackcentury.shinebuttonlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet

/**
 * PorterShapeImageView extends PorterImageView to provide shape masking via a Drawable.
 */
open class PorterShapeImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : PorterImageView(context, attrs, defStyle) {

    private var shape: Drawable? = null
    private var matrix: Matrix = Matrix()
    private var drawMatrix: Matrix? = null

    init {
        setup(context, attrs, defStyle)
    }

    private fun setup(context: Context, attrs: AttributeSet?, defStyle: Int) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.PorterImageView, defStyle, 0)
            shape = typedArray.getDrawable(R.styleable.PorterImageView_siShape)
            typedArray.recycle()
        }
    }

    fun setShape(drawable: Drawable?) {
        shape = drawable
        invalidate()
    }

    override fun paintMaskCanvas(maskCanvas: Canvas, maskPaint: Paint, width: Int, height: Int) {
        shape?.let {
            configureDrawableBounds(getWidth(), getHeight())
            if (drawMatrix != null) {
                val drawableSaveCount = maskCanvas.save()
                maskCanvas.concat(matrix)
                it.draw(maskCanvas)
                maskCanvas.restoreToCount(drawableSaveCount)
            } else {
                it.draw(maskCanvas)
            }
        }
    }

    private fun configureDrawableBounds(viewWidth: Int, viewHeight: Int) {
        drawMatrix = null
        val s = shape ?: return
        val drawableWidth = s.intrinsicWidth
        val drawableHeight = s.intrinsicHeight

        if (drawableWidth <= 0 || drawableHeight <= 0) {
            // For drawables without intrinsic size (like some vectors or solid colors), 
            // just fit to view bounds
            s.setBounds(0, 0, viewWidth, viewHeight)
            return
        }

        val fits = viewWidth == drawableWidth && viewHeight == drawableHeight
        if (!fits) {
            s.setBounds(0, 0, drawableWidth, drawableHeight)
            val widthRatio = viewWidth.toFloat() / drawableWidth.toFloat()
            val heightRatio = viewHeight.toFloat() / drawableHeight.toFloat()
            
            // Use Center Crop logic: take the larger scale and center it
            val scale = Math.max(widthRatio, heightRatio)
            val dx = ((viewWidth - drawableWidth * scale) * 0.5f + 0.5f)
            val dy = ((viewHeight - drawableHeight * scale) * 0.5f + 0.5f)

            matrix.setScale(scale, scale)
            matrix.postTranslate(dx, dy)
            drawMatrix = matrix
        }
    }
}
