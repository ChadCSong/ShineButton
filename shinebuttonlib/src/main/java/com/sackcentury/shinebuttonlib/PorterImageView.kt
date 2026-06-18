package com.sackcentury.shinebuttonlib

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView

/**
 * PorterImageView is an abstract base class that uses PorterDuff Xfermodes to create custom-shaped ImageViews.
 */
abstract class PorterImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    companion object {
        private val TAG = PorterImageView::class.java.simpleName
        private val PORTER_DUFF_XFERMODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private var maskCanvas: Canvas? = null
    private var maskBitmap: Bitmap? = null
    private var maskPaint: Paint? = null

    private var drawableCanvas: Canvas? = null
    private var drawableBitmap: Bitmap? = null
    private var drawablePaint: Paint? = null

    protected var paintColor = Color.GRAY
    private var invalidated = true

    init {
        setup()
    }

    private fun setup() {
        if (scaleType == ScaleType.FIT_CENTER) {
            scaleType = ScaleType.CENTER_CROP
        }

        maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
        }
    }

    fun setSrcColor(color: Int) {
        paintColor = color
        setImageDrawable(ColorDrawable(color))
        drawablePaint?.let {
            it.color = color
            invalidate()
        }
    }

    override fun invalidate() {
        invalidated = true
        super.invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createMaskCanvas(w, h, oldw, oldh)
    }

    private fun createMaskCanvas(width: Int, height: Int, oldw: Int, oldh: Int) {
        val sizeChanged = width != oldw || height != oldh
        val isValid = width > 0 && height > 0
        if (isValid && (maskCanvas == null || sizeChanged)) {
            maskCanvas = Canvas()
            maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
                maskCanvas?.setBitmap(it)
            }

            maskPaint?.reset()
            maskCanvas?.let { canvas ->
                maskPaint?.let { paint ->
                    paintMaskCanvas(canvas, paint, width, height)
                }
            }

            drawableCanvas = Canvas()
            drawableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
                drawableCanvas?.setBitmap(it)
            }
            drawablePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = paintColor
            }
            invalidated = true
        }
    }

    protected abstract fun paintMaskCanvas(maskCanvas: Canvas, maskPaint: Paint, width: Int, height: Int)

    override fun onDraw(canvas: Canvas) {
        if (!isInEditMode) {
            try {
                if (invalidated) {
                    val drawable = drawable
                    if (drawable != null) {
                        invalidated = false
                        val imageMatrix = imageMatrix
                        if (imageMatrix == null) {
                            drawableCanvas?.let { drawable.draw(it) }
                        } else {
                            val drawableSaveCount = drawableCanvas?.save() ?: 0
                            drawableCanvas?.concat(imageMatrix)
                            drawableCanvas?.let { drawable.draw(it) }
                            drawableCanvas?.restoreToCount(drawableSaveCount)
                        }

                        drawablePaint?.apply {
                            reset()
                            isFilterBitmap = false
                            xfermode = PORTER_DUFF_XFERMODE
                        }
                        maskBitmap?.let {
                            drawablePaint?.let { paint ->
                                drawableCanvas?.drawBitmap(it, 0.0f, 0.0f, paint)
                            }
                        }
                    }
                }

                if (!invalidated) {
                    drawablePaint?.xfermode = null
                    drawableBitmap?.let {
                        drawablePaint?.let { paint ->
                            canvas.drawBitmap(it, 0.0f, 0.0f, paint)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception occurred while drawing $id", e)
            }
        } else {
            super.onDraw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var wSpec = widthMeasureSpec
        var hSpec = heightMeasureSpec
        if (wSpec == 0) wSpec = 50
        if (hSpec == 0) hSpec = 50
        super.onMeasure(wSpec, hSpec)
    }
}
