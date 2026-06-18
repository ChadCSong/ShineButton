package com.sackcentury.shinebuttonlib

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import com.sackcentury.shinebuttonlib.interpolator.QuartOutInterpolator
import com.sackcentury.shinebuttonlib.listener.SimpleAnimatorListener
import java.util.*

/**
 * ShineView is a transparent view that overlays the activity/dialog to draw the shine animation particles.
 * It uses two animators: one for the expanding shine particles and one for the button's "click" splash effect.
 *
 * @author Chad
 * @since 16/7/5
 **/
class ShineView : View {

    companion object {
        private const val TAG = "ShineView"

        /**
         * Delay between frame refreshes in milliseconds. Increased to 25ms to save CPU.
         */
        private const val FRAME_REFRESH_DELAY: Long = 25

        private const val COLOR_COUNT = 10
        private val colorRandom = IntArray(COLOR_COUNT)

        init {
            colorRandom[0] = Color.parseColor("#FFFF99")
            colorRandom[1] = Color.parseColor("#FFCCCC")
            colorRandom[2] = Color.parseColor("#996699")
            colorRandom[3] = Color.parseColor("#FF6666")
            colorRandom[4] = Color.parseColor("#FFFF66")
            colorRandom[5] = Color.parseColor("#F44336")
            colorRandom[6] = Color.parseColor("#666666")
            colorRandom[7] = Color.parseColor("#CCCC00")
            colorRandom[8] = Color.parseColor("#666666")
            colorRandom[9] = Color.parseColor("#999933")
        }
    }

    private var shineAnimator: ShineAnimator? = null
    private var clickAnimator: ValueAnimator? = null

    private var shineButton: ShineButton? = null
    private lateinit var paint: Paint
    private lateinit var paint2: Paint
    private lateinit var paintSmall: Paint

    // Animation configuration properties
    private var shineCount: Int = 0
    private var smallOffsetAngle: Float = 0f
    private var turnAngle: Float = 0f
    private var animDuration: Long = 0
    private var clickAnimDuration: Long = 0
    private var shineDistanceMultiple: Float = 0f
    private var smallShineColor = colorRandom[0]
    private var bigShineColor = colorRandom[1]
    private var shineSize = 0
    private var allowRandomColor = false
    private var enableFlashing = false
    private var customInterpolator: Interpolator? = null

    private val rectF = RectF()
    private val rectFSmall = RectF()

    private val random = Random()
    private var centerAnimX: Int = 0
    private var centerAnimY: Int = 0
    private var btnWidth: Int = 0
    private var btnHeight: Int = 0

    private var value: Float = 0f
    private var clickValue = 0f
    private var isRun = false
    private val distanceOffset = 0.2f

    constructor(context: Context) : super(context)

    /**
     * Creates a ShineView and initializes the particle and splash animators.
     *
     * @param context     The context.
     * @param shineButton The target button.
     * @param shineParams Configuration for the animation.
     */
    constructor(context: Context, shineButton: ShineButton, shineParams: ShineParams) : super(context) {
        initShineParams(shineParams, shineButton)
        this.shineAnimator = ShineAnimator(animDuration, shineDistanceMultiple, clickAnimDuration)
        this.shineAnimator?.interpolator = customInterpolator ?: QuartOutInterpolator()
        this.shineButton = shineButton

        // Main shine particles paint
        paint = Paint().apply {
            color = bigShineColor
            strokeWidth = 20f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        // Click splash center paint
        paint2 = Paint().apply {
            color = Color.WHITE
            strokeWidth = 20f
            strokeCap = Paint.Cap.ROUND
        }

        // Secondary small shine particles paint
        paintSmall = Paint().apply {
            color = smallShineColor
            strokeWidth = 10f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        // Click splash animation (expanding circle)
        clickAnimator = ValueAnimator.ofFloat(0f, 1.1f).apply {
            duration = clickAnimDuration
            interpolator = customInterpolator ?: QuartOutInterpolator()
            addUpdateListener { valueAnimator ->
                clickValue = valueAnimator.animatedValue as Float
                invalidate()
            }
            addListener(object : SimpleAnimatorListener() {
                override fun onAnimationEnd(animator: Animator) {
                    clickValue = 0f
                    invalidate()
                }
            })
        }

        // Shine particle animation callback
        shineAnimator?.addListener(object : SimpleAnimatorListener() {
            override fun onAnimationEnd(animator: Animator) {
                // Remove the view from the root once animation is done
                (parent as? ViewGroup)?.removeView(this@ShineView)
            }
        })
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * Calculates positions and starts the animations.
     *
     * @param shineButton The button to animate around.
     */
    fun showAnimation(shineButton: ShineButton) {
        btnWidth = shineButton.width
        btnHeight = shineButton.height

        val location = IntArray(2)
        shineButton.getLocationInWindow(location)
        centerAnimX = location[0] + shineButton.width / 2
        centerAnimY = location[1] + shineButton.height / 2

        // If in a dialog, adjust coordinate offsets
        shineButton.mFixDialog?.window?.let { window ->
            val decor = window.decorView
            centerAnimX -= decor.paddingLeft
            centerAnimY -= decor.paddingTop
        }

        shineAnimator?.addUpdateListener { valueAnimator ->
            value = valueAnimator.animatedValue as Float
            // Dynamically adjust particle stroke width as they expand
            if (shineSize > 0) {
                paint.strokeWidth = shineSize * (shineDistanceMultiple - value)
                paintSmall.strokeWidth = (shineSize.toFloat() / 3f * 2f) * (shineDistanceMultiple - value)
            } else {
                paint.strokeWidth = (btnWidth.toFloat() / 2.0f) * (shineDistanceMultiple - value)
                paintSmall.strokeWidth = (btnWidth.toFloat() / 3.0f) * (shineDistanceMultiple - value)
            }

            // Calculate expanding rects for the arcs (particles)
            val xOffset = btnWidth.toFloat() / (3f - shineDistanceMultiple) * value
            val yOffset = btnHeight.toFloat() / (3f - shineDistanceMultiple) * value
            rectF.set(centerAnimX - xOffset, centerAnimY - yOffset, centerAnimX + xOffset, centerAnimY + yOffset)

            val xOffsetSmall = btnWidth.toFloat() / ((3f - shineDistanceMultiple) + distanceOffset) * value
            val yOffsetSmall = btnHeight.toFloat() / ((3f - shineDistanceMultiple) + distanceOffset) * value
            rectFSmall.set(centerAnimX - xOffsetSmall, centerAnimY - yOffsetSmall, centerAnimX + xOffsetSmall, centerAnimY + yOffsetSmall)

            invalidate()
        }
        shineAnimator?.startAnim()
        clickAnimator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw primary shine particles
        for (i in 0 until shineCount) {
            if (allowRandomColor) {
                val index = Math.abs(COLOR_COUNT / 2 - i).let { if (it >= COLOR_COUNT) COLOR_COUNT - 1 else it }
                paint.color = colorRandom[index]
            }
            canvas.drawArc(rectF, 360f / shineCount * i + 1 + (value - 1) * turnAngle, 0.1f, false, getConfigPaint(paint))
        }

        // Draw secondary (small) shine particles
        for (i in 0 until shineCount) {
            if (allowRandomColor) {
                val index = Math.abs(COLOR_COUNT / 2 - i).let { if (it >= COLOR_COUNT) COLOR_COUNT - 1 else it }
                paintSmall.color = colorRandom[index]
            }
            canvas.drawArc(rectFSmall, 360f / shineCount * i + 1 - smallOffsetAngle + (value - 1) * turnAngle, 0.1f, false, getConfigPaint(paintSmall))
        }

        // Draw click splash point
        paint.strokeWidth = btnWidth * clickValue * (shineDistanceMultiple - distanceOffset)
        if (clickValue != 0f) {
            paint2.strokeWidth = btnWidth * clickValue * (shineDistanceMultiple - distanceOffset) - 8
        } else {
            paint2.strokeWidth = 0f
        }
        canvas.drawPoint(centerAnimX.toFloat(), centerAnimY.toFloat(), paint)
        canvas.drawPoint(centerAnimX.toFloat(), centerAnimY.toFloat(), paint2)

        // Trigger animation on first draw if not already running
        if (shineAnimator != null && !isRun) {
            isRun = true
            shineButton?.let { showAnimation(it) }
        }
    }

    private fun getConfigPaint(paint: Paint): Paint {
        if (enableFlashing) {
            paint.color = colorRandom[random.nextInt(COLOR_COUNT - 1)]
        }
        return paint
    }

    /**
     * Container for shine animation parameters.
     */
    class ShineParams {
        var allowRandomColor = false
        var animDuration: Long = 1500
        var bigShineColor = 0
        var clickAnimDuration: Long = 200
        var enableFlashing = false
        var shineCount = 7
        var shineTurnAngle = 20f
        var shineDistanceMultiple = 1.5f
        var smallShineOffsetAngle = 20f
        var smallShineColor = 0
        var shineSize = 0
        var customInterpolator: Interpolator? = null
    }

    /**
     * Initializes internal properties from the provided ShineParams.
     */
    private fun initShineParams(shineParams: ShineParams, shineButton: ShineButton) {
        shineCount = shineParams.shineCount
        turnAngle = shineParams.shineTurnAngle
        smallOffsetAngle = shineParams.smallShineOffsetAngle
        enableFlashing = shineParams.enableFlashing
        allowRandomColor = shineParams.allowRandomColor
        shineDistanceMultiple = shineParams.shineDistanceMultiple
        animDuration = shineParams.animDuration
        clickAnimDuration = shineParams.clickAnimDuration
        smallShineColor = shineParams.smallShineColor
        bigShineColor = shineParams.bigShineColor
        shineSize = shineParams.shineSize
        customInterpolator = shineParams.customInterpolator

        if (smallShineColor == 0) {
            smallShineColor = colorRandom[6]
        }

        if (bigShineColor == 0) {
            bigShineColor = shineButton.color
        }
    }
}
