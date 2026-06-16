package com.sackcentury.shinebuttonlib

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.LinearInterpolator
import androidx.core.content.res.ResourcesCompat
import com.sackcentury.shinebuttonlib.listener.SimpleAnimatorListener

/**
 * ShineButton is a customizable animated button with a "shine" effect.
 * It inherits from [PorterShapeImageView] to support custom shapes via PNG masks.
 *
 * @author Chad
 * @since 16/7/5
 **/
class ShineButton : PorterShapeImageView {

    companion object {
        private const val TAG = "ShineButton"
        private const val DEFAULT_WIDTH = 50
        private const val DEFAULT_HEIGHT = 50
    }

    /**
     * Whether the button is currently in the "checked" (filled) state.
     */
    var isChecked = false
        private set

    /**
     * The color of the button in its normal (unchecked) state.
     */
    private var btnColor: Int = 0

    /**
     * The color of the button when it is checked.
     */
    private var btnFillColor: Int = 0

    private val metrics = DisplayMetrics()

    /**
     * The host activity, used to add the [ShineView] to the root layout.
     */
    private var activity: Activity? = null

    /**
     * The view responsible for rendering the shine particles.
     */
    private var shineView: ShineView? = null

    /**
     * Animator for the button's scale/shake effect when clicked.
     */
    private var shakeAnimator: ValueAnimator? = null

    /**
     * Parameters controlling the shine animation's appearance.
     */
    private val shineParams = ShineView.ShineParams()

    /**
     * Listener for state changes.
     */
    private var listener: OnCheckedChangeListener? = null

    private var bottomHeight: Int = 0
    private var realBottomHeight: Int = 0

    /**
     * Reference to a parent dialog, if any, to fix positioning issues.
     */
    @JvmField
    var mFixDialog: Dialog? = null

    private var onButtonClickListener: OnButtonClickListener? = null

    val color: Int
        get() = btnFillColor

    constructor(context: Context) : super(context) {
        if (context is Activity) {
            init(context)
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initButton(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initButton(context, attrs)
    }

    /**
     * Initializes the button attributes from XML.
     */
    private fun initButton(context: Context, attrs: AttributeSet?) {
        if (context is Activity) {
            init(context)
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.ShineButton)
        btnColor = a.getColor(R.styleable.ShineButton_btn_color, Color.GRAY)
        btnFillColor = a.getColor(R.styleable.ShineButton_btn_fill_color, Color.BLACK)

        // Map XML attributes to ShineParams
        shineParams.allowRandomColor = a.getBoolean(R.styleable.ShineButton_allow_random_color, false)
        shineParams.animDuration = a.getInteger(R.styleable.ShineButton_shine_animation_duration, shineParams.animDuration.toInt()).toLong()
        shineParams.bigShineColor = a.getColor(R.styleable.ShineButton_big_shine_color, shineParams.bigShineColor)
        shineParams.clickAnimDuration = a.getInteger(R.styleable.ShineButton_click_animation_duration, shineParams.clickAnimDuration.toInt()).toLong()
        shineParams.enableFlashing = a.getBoolean(R.styleable.ShineButton_enable_flashing, false)
        shineParams.shineCount = a.getInteger(R.styleable.ShineButton_shine_count, shineParams.shineCount)
        shineParams.shineDistanceMultiple = a.getFloat(R.styleable.ShineButton_shine_distance_multiple, shineParams.shineDistanceMultiple)
        shineParams.shineTurnAngle = a.getFloat(R.styleable.ShineButton_shine_turn_angle, shineParams.shineTurnAngle)
        shineParams.smallShineColor = a.getColor(R.styleable.ShineButton_small_shine_color, shineParams.smallShineColor)
        shineParams.smallShineOffsetAngle = a.getFloat(R.styleable.ShineButton_small_shine_offset_angle, shineParams.smallShineOffsetAngle)
        shineParams.shineSize = a.getDimensionPixelSize(R.styleable.ShineButton_shine_size, shineParams.shineSize)
        a.recycle()

        setSrcColor(btnColor)
    }

    /**
     * Sets a reference to the host dialog to ensure the shine animation is placed correctly.
     *
     * @param fixDialog The parent dialog.
     */
    fun setFixDialog(fixDialog: Dialog?) {
        mFixDialog = fixDialog
    }

    fun getBottomHeight(real: Boolean): Int {
        return if (real) realBottomHeight else bottomHeight
    }

    /**
     * Sets the base color of the button.
     */
    fun setBtnColor(btnColor: Int) {
        this.btnColor = btnColor
        setSrcColor(this.btnColor)
    }

    /**
     * Sets the fill color used when the button is checked.
     */
    fun setBtnFillColor(btnFillColor: Int) {
        this.btnFillColor = btnFillColor
    }

    /**
     * Updates the checked state with an optional animation.
     */
    @JvmOverloads
    fun setChecked(checked: Boolean, anim: Boolean = false) {
        setChecked(checked, anim, true)
    }

    /**
     * Internal setChecked implementation.
     *
     * @param checked  The new state.
     * @param anim     Whether to show the shine/shake animation.
     * @param callBack Whether to notify the listener.
     */
    private fun setChecked(checked: Boolean, anim: Boolean, callBack: Boolean) {
        isChecked = checked
        if (checked) {
            setSrcColor(btnFillColor)
            if (anim) showAnim()
        } else {
            setSrcColor(btnColor)
            if (anim) setCancel()
        }
        if (callBack) {
            onListenerUpdate(checked)
        }
    }

    private fun onListenerUpdate(checked: Boolean) {
        listener?.onCheckedChanged(this, checked)
    }

    /**
     * Cancels the animation and reverts to the unchecked state.
     */
    fun setCancel() {
        setSrcColor(btnColor)
        shakeAnimator?.apply {
            end()
            cancel()
        }
    }

    // --- Configuration setters for ShineParams ---

    fun setAllowRandomColor(allowRandomColor: Boolean) {
        shineParams.allowRandomColor = allowRandomColor
    }

    fun setAnimDuration(durationMs: Int) {
        shineParams.animDuration = durationMs.toLong()
    }

    fun setBigShineColor(color: Int) {
        shineParams.bigShineColor = color
    }

    fun setClickAnimDuration(durationMs: Int) {
        shineParams.clickAnimDuration = durationMs.toLong()
    }

    fun enableFlashing(enable: Boolean) {
        shineParams.enableFlashing = enable
    }

    fun setShineCount(count: Int) {
        shineParams.shineCount = count
    }

    fun setShineDistanceMultiple(multiple: Float) {
        shineParams.shineDistanceMultiple = multiple
    }

    fun setShineTurnAngle(angle: Float) {
        shineParams.shineTurnAngle = angle
    }

    fun setSmallShineColor(color: Int) {
        shineParams.smallShineColor = color
    }

    fun setSmallShineOffAngle(angle: Float) {
        shineParams.smallShineOffsetAngle = angle
    }

    fun setShineSize(size: Int) {
        shineParams.shineSize = size
    }

    override fun setOnClickListener(l: OnClickListener?) {
        if (l is OnButtonClickListener) {
            super.setOnClickListener(l)
        } else {
            onButtonClickListener?.setListener(l)
        }
    }

    fun setOnCheckStateChangeListener(listener: OnCheckedChangeListener?) {
        this.listener = listener
    }

    /**
     * Initializes the button. Must be called after the button is created programmatically or from XML.
     *
     * @param activity The host activity.
     */
    fun init(activity: Activity) {
        this.activity = activity
        onButtonClickListener = OnButtonClickListener()
        setOnClickListener(onButtonClickListener)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        calPixels()
    }

    /**
     * Starts the shine and shake animations.
     */
    fun showAnim() {
        if (activity == null && context is Activity) {
            activity = context as Activity
        }
        activity?.let { act ->
            shineView = ShineView(act, this, shineParams)
            val rootView: ViewGroup
            // Determine where to add the ShineView (Dialog root or Activity root)
            val dialog = mFixDialog
            if (dialog?.window != null) {
                rootView = dialog.window!!.decorView as ViewGroup
                val innerView = rootView.findViewById<View>(android.R.id.content)
                if (innerView != null) {
                    rootView.addView(shineView, ViewGroup.LayoutParams(innerView.width, innerView.getHeight()))
                } else {
                    rootView.addView(shineView, ViewGroup.LayoutParams(rootView.width, rootView.getHeight()))
                }
            } else {
                rootView = act.window.decorView as ViewGroup
                rootView.addView(shineView, ViewGroup.LayoutParams(rootView.width, rootView.getHeight()))
            }
            doShareAnim()
        } ?: run {
            Log.e(TAG, "ShineButton must be initialized with an Activity context to show animation.")
        }
    }

    /**
     * Sets the shape of the button using a raw resource ID (PNG mask).
     */
    fun setShapeResource(raw: Int) {
        setShape(ResourcesCompat.getDrawable(resources, raw, null))
    }

    /**
     * Executes the button's scale/shake animation.
     */
    private fun doShareAnim() {
        shakeAnimator = ValueAnimator.ofFloat(0.4f, 1f, 0.9f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = 500
            startDelay = 180
            addUpdateListener { valueAnimator ->
                val v = valueAnimator.animatedValue as Float
                scaleX = v
                scaleY = v
            }
            addListener(object : SimpleAnimatorListener() {
                override fun onAnimationStart(animator: Animator) {
                    setSrcColor(btnFillColor)
                }

                override fun onAnimationEnd(animator: Animator) {
                    setSrcColor(if (isChecked) btnFillColor else btnColor)
                }

                override fun onAnimationCancel(animator: Animator) {
                    setSrcColor(btnColor)
                }
            })
            start()
        }
        invalidate()
    }

    /**
     * Calculates position and visibility metrics to assist with positioning the shine animation.
     */
    private fun calPixels() {
        activity?.let {
            it.windowManager.defaultDisplay.getMetrics(metrics)
            val location = IntArray(2)
            getLocationInWindow(location)
            val visibleFrame = Rect()
            it.window.decorView.getWindowVisibleDisplayFrame(visibleFrame)
            realBottomHeight = visibleFrame.height() - location[1]
            bottomHeight = metrics.heightPixels - location[1]
        }
    }

    /**
     * Internal click listener to handle the toggle logic and animations.
     */
    inner class OnButtonClickListener : OnClickListener {
        private var listener: OnClickListener? = null

        fun setListener(listener: OnClickListener?) {
            this.listener = listener
        }

        override fun onClick(view: View) {
            if (!isChecked) {
                isChecked = true
                showAnim()
            } else {
                isChecked = false
                setCancel()
            }
            onListenerUpdate(isChecked)
            listener?.onClick(view)
        }
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(view: View, checked: Boolean)
    }
}
