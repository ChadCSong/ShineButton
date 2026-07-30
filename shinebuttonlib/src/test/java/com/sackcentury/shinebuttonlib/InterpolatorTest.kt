package com.sackcentury.shinebuttonlib

import com.sackcentury.shinebuttonlib.interpolator.QuartOutInterpolator
import org.junit.Assert.assertEquals
import org.junit.Test

class InterpolatorTest {

    @Test
    fun quartOutInterpolator_calculatesCorrectFractionAtBoundaries() {
        val interpolator = QuartOutInterpolator()
        assertEquals(0.0f, interpolator.getInterpolation(0.0f), 0.001f)
        assertEquals(1.0f, interpolator.getInterpolation(1.0f), 0.001f)
    }

    @Test
    fun quartOutInterpolator_calculatesMidpoint() {
        val interpolator = QuartOutInterpolator()
        val mid = interpolator.getInterpolation(0.5f)
        // QuartOut at 0.5 is 1 - (-0.5)^4 = 1 - 0.0625 = 0.9375
        assertEquals(0.9375f, mid, 0.001f)
    }
}
