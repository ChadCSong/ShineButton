package com.sackcentury.shinebuttonlib

import android.animation.ValueAnimator
import android.graphics.Canvas
import com.sackcentury.shinebuttonlib.interpolator.QuartOutInterpolator

/**
 * ShineAnimator handles the value animation for the shine effect particles.
 *
 * @author Chad
 * @since 16/7/5
 **/
class ShineAnimator : ValueAnimator {

    private var maxValue = 1.5f
    private var animDuration: Long = 1500
    private var canvas: Canvas? = null

    constructor() {
        setFloatValues(1f, maxValue)
        duration = animDuration
        startDelay = 200
        interpolator = QuartOutInterpolator()
    }

    constructor(durationMs: Long, maxValue: Float, delay: Long) {
        this.animDuration = durationMs
        this.maxValue = maxValue
        setFloatValues(1f, maxValue)
        duration = durationMs
        startDelay = delay
        interpolator = QuartOutInterpolator()
    }

    fun startAnim() {
        start()
    }

    fun setCanvas(canvas: Canvas?) {
        this.canvas = canvas
    }
}
