package com.sackcentury.shinebuttonlib

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShineParamsTest {

    @Test
    fun defaultParams_areCorrect() {
        val params = ShineView.ShineParams()
        assertFalse(params.allowRandomColor)
        assertEquals(1500L, params.animDuration)
        assertEquals(0, params.bigShineColor)
        assertEquals(200L, params.clickAnimDuration)
        assertFalse(params.enableFlashing)
        assertEquals(7, params.shineCount)
        assertEquals(20f, params.shineTurnAngle, 0.001f)
        assertEquals(1.5f, params.shineDistanceMultiple, 0.001f)
        assertEquals(20f, params.smallShineOffsetAngle, 0.001f)
        assertEquals(0, params.smallShineColor)
        assertEquals(0, params.shineSize)
        assertNull(params.customInterpolator)
    }

    @Test
    fun customParams_canBeMutated() {
        val params = ShineView.ShineParams().apply {
            allowRandomColor = true
            animDuration = 3000L
            bigShineColor = 0xFF0000
            clickAnimDuration = 400L
            enableFlashing = true
            shineCount = 12
            shineTurnAngle = 45f
            shineDistanceMultiple = 2.0f
            smallShineOffsetAngle = 15f
            smallShineColor = 0x00FF00
            shineSize = 80
        }

        assertTrue(params.allowRandomColor)
        assertEquals(3000L, params.animDuration)
        assertEquals(0xFF0000, params.bigShineColor)
        assertEquals(400L, params.clickAnimDuration)
        assertTrue(params.enableFlashing)
        assertEquals(12, params.shineCount)
        assertEquals(45f, params.shineTurnAngle, 0.001f)
        assertEquals(2.0f, params.shineDistanceMultiple, 0.001f)
        assertEquals(15f, params.smallShineOffsetAngle, 0.001f)
        assertEquals(0x00FF00, params.smallShineColor)
        assertEquals(80, params.shineSize)
    }
}
