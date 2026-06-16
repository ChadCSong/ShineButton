package com.sackcentury.shinebuttonlib.interpolator

import android.view.animation.Interpolator
import kotlin.math.pow

/**
 * A local implementation of the QuartOut easing function to remove the dependency
 * on the external EasingInterpolator library which was causing JitPack 403 errors.
 */
class QuartOutInterpolator : Interpolator {
    override fun getInterpolation(input: Float): Float {
        val t = input - 1f
        return -(t.pow(3) * t - 1f)
    }
}