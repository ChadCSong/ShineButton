package com.sackcentury.shinebuttonlib

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.translate

/**
 * A native Jetpack Compose implementation of the ShineButton.
 *
 * @param isChecked The current checked state.
 * @param onCheckedChange Callback when the state changes.
 * @param modifier Modifier for the button.
 * @param shape The Vector image to use for the button icon (e.g., Icons.Default.Favorite).
 * @param btnColor Base color of the button (unchecked).
 * @param btnFillColor Color when the button is checked.
 * @param shineColor Primary color for shine particles.
 * @param shineSize Size of the button.
 * @param shineCount Number of shine particles.
 */
@Composable
fun ShineButtonCompose(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: ImageVector,
    modifier: Modifier = Modifier,
    btnColor: Color = Color.Gray,
    btnFillColor: Color = Color.Red,
    shineColor: Color = Color.Red,
    shineSize: Dp = 50.dp,
    shineCount: Int = 8,
    animDuration: Int = 1000,
    allowRandomColor: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val painter = rememberVectorPainter(image = shape)

    // Animation states
    val transition = updateTransition(targetState = isChecked, label = "ShineTransition")

    val shineProgress by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                tween(durationMillis = animDuration, easing = LinearOutSlowInEasing)
            } else {
                tween(durationMillis = 0)
            }
        },
        label = "ShineProgress"
    ) { checked ->
        if (checked) 1f else 0f
    }

    val scale by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                keyframes {
                    durationMillis = 500
                    0.4f at 0
                    1.1f at 250
                    1.0f at 500
                }
            } else {
                tween(durationMillis = 200)
            }
        },
        label = "Scale"
    ) { checked ->
        1.0f
    }

    val particleColors = remember(allowRandomColor) {
        if (allowRandomColor) {
            listOf(
                Color(0xFFFFFF99), Color(0xFFFFCCCC), Color(0xFF996699),
                Color(0xFFFF6666), Color(0xFFFFFF66), Color(0xFFF44336),
                Color(0xFF666666), Color(0xFFCCCC00)
            )
        } else {
            listOf(shineColor)
        }
    }

    Box(
        modifier = modifier
            .size(shineSize)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onCheckedChange(!isChecked)
            },
        contentAlignment = Alignment.Center
    ) {
        // Shine Particles Layer
        if (shineProgress > 0f && shineProgress < 1f) {
            Canvas(modifier = Modifier.size(shineSize * 2.5f)) {
                val center = Offset(size.width / 2, size.height / 2)
                val maxRadius = (shineSize.toPx() * 1.2f)
                val currentRadius = maxRadius * shineProgress

                for (i in 0 until shineCount) {
                    val angle = (360f / shineCount) * i
                    val angleRad = angle * (PI / 180f).toFloat()

                    val x = center.x + cos(angleRad) * currentRadius
                    val y = center.y + sin(angleRad) * currentRadius

                    val color = if (allowRandomColor) particleColors[i % particleColors.size] else shineColor
                    
                    // Main particle
                    drawCircle(
                        color = color,
                        radius = 3.dp.toPx() * (1f - shineProgress),
                        center = Offset(x, y)
                    )

                    // Secondary small particle
                    val offsetAngleRad = (angle + 20f) * (PI / 180f).toFloat()
                    val x2 = center.x + cos(offsetAngleRad) * (currentRadius * 0.85f)
                    val y2 = center.y + sin(offsetAngleRad) * (currentRadius * 0.85f)
                    
                    drawCircle(
                        color = color.copy(alpha = 0.6f),
                        radius = 1.5.dp.toPx() * (1f - shineProgress),
                        center = Offset(x2, y2)
                    )
                }
            }
        }

        // Center Icon Layer
        val iconColor = if (isChecked) btnFillColor else btnColor
        Box(
            modifier = Modifier
                .size(shineSize)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                with(painter) {
                    draw(
                        size = size,
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(iconColor)
                    )
                }
            }
        }
    }
}
