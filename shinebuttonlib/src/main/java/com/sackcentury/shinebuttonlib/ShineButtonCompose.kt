package com.sackcentury.shinebuttonlib

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * A native Jetpack Compose implementation of the ShineButton accepting an [ImageVector].
 */
@Composable
fun ShineButtonCompose(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    btnColor: Color = Color.Gray,
    btnFillColor: Color = Color.Red,
    shineColor: Color = Color.Red,
    shineSize: Dp = 50.dp,
    shineCount: Int = 8,
    animDuration: Int = 1000,
    allowRandomColor: Boolean = false,
    shineAnimationSpec: FiniteAnimationSpec<Float>? = null,
    scaleAnimationSpec: FiniteAnimationSpec<Float>? = null
) {
    val painter = rememberVectorPainter(image = shape)
    ShineButtonCompose(
        isChecked = isChecked,
        onCheckedChange = onCheckedChange,
        painter = painter,
        modifier = modifier,
        contentDescription = contentDescription,
        btnColor = btnColor,
        btnFillColor = btnFillColor,
        shineColor = shineColor,
        shineSize = shineSize,
        shineCount = shineCount,
        animDuration = animDuration,
        allowRandomColor = allowRandomColor,
        shineAnimationSpec = shineAnimationSpec,
        scaleAnimationSpec = scaleAnimationSpec
    )
}

/**
 * A native Jetpack Compose implementation of the ShineButton accepting a Drawable resource ID.
 */
@Composable
fun ShineButtonCompose(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    @DrawableRes shapeRes: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    btnColor: Color = Color.Gray,
    btnFillColor: Color = Color.Red,
    shineColor: Color = Color.Red,
    shineSize: Dp = 50.dp,
    shineCount: Int = 8,
    animDuration: Int = 1000,
    allowRandomColor: Boolean = false,
    shineAnimationSpec: FiniteAnimationSpec<Float>? = null,
    scaleAnimationSpec: FiniteAnimationSpec<Float>? = null
) {
    val painter = painterResource(id = shapeRes)
    ShineButtonCompose(
        isChecked = isChecked,
        onCheckedChange = onCheckedChange,
        painter = painter,
        modifier = modifier,
        contentDescription = contentDescription,
        btnColor = btnColor,
        btnFillColor = btnFillColor,
        shineColor = shineColor,
        shineSize = shineSize,
        shineCount = shineCount,
        animDuration = animDuration,
        allowRandomColor = allowRandomColor,
        shineAnimationSpec = shineAnimationSpec,
        scaleAnimationSpec = scaleAnimationSpec
    )
}

/**
 * A native Jetpack Compose implementation of the ShineButton accepting a generic [Painter].
 */
@Composable
fun ShineButtonCompose(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    btnColor: Color = Color.Gray,
    btnFillColor: Color = Color.Red,
    shineColor: Color = Color.Red,
    shineSize: Dp = 50.dp,
    shineCount: Int = 8,
    animDuration: Int = 1000,
    allowRandomColor: Boolean = false,
    shineAnimationSpec: FiniteAnimationSpec<Float>? = null,
    scaleAnimationSpec: FiniteAnimationSpec<Float>? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    // Accessibility descriptions
    val defaultDescription = contentDescription ?: "Shine button"
    val defaultStateDescription = if (isChecked) "Checked" else "Unchecked"

    // Animation states
    val transition = updateTransition(targetState = isChecked, label = "ShineTransition")

    val shineProgress by transition.animateFloat(
        transitionSpec = {
            if (targetState) {
                shineAnimationSpec ?: tween(durationMillis = animDuration, easing = LinearOutSlowInEasing)
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
                scaleAnimationSpec ?: keyframes {
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
        if (checked) 1.0f else 1.0f
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
            .semantics {
                role = Role.Checkbox
                this.contentDescription = defaultDescription
                this.stateDescription = defaultStateDescription
            }
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
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                }
            }
        }
    }
}

// --- IDE Previews ---

@Preview(name = "Unchecked State", showBackground = true)
@Composable
private fun ShineButtonComposeUncheckedPreview() {
    var checked by remember { mutableStateOf(false) }
    ShineButtonCompose(
        isChecked = checked,
        onCheckedChange = { checked = it },
        shape = Icons.Default.Favorite,
        btnColor = Color.Gray,
        btnFillColor = Color.Red
    )
}

@Preview(name = "Checked State", showBackground = true)
@Composable
private fun ShineButtonComposeCheckedPreview() {
    var checked by remember { mutableStateOf(true) }
    ShineButtonCompose(
        isChecked = checked,
        onCheckedChange = { checked = it },
        shape = Icons.Default.Favorite,
        btnColor = Color.Gray,
        btnFillColor = Color.Red
    )
}
