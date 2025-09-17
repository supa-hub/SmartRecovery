package com.kunto.smartrecovery

import androidx.compose.foundation.background
import com.kunto.smartrecovery.theming.Blue
import com.kunto.smartrecovery.theming.LightBlue
import com.kunto.smartrecovery.theming.White

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.BeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kunto.smartrecovery.theming.Black
import com.kunto.smartrecovery.theming.backgroundColorDark


@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Blue,
            primaryContainer = backgroundColorDark,
            secondary = White,
            secondaryContainer = backgroundColorDark,
            onSecondary = LightBlue,
            onSecondaryContainer = LightBlue,
            tertiary = Black,
            surface = backgroundColorDark,
            onSurface = White,
            onSurfaceVariant = White,
            surfaceContainerHigh = backgroundColorDark,
            surfaceContainerLow = backgroundColorDark,
            inverseSurface = White,
            background = backgroundColorDark
        )
    } else {
        lightColorScheme(
            primary = Blue,
            primaryContainer = White,
            secondary = Blue,
            secondaryContainer = White,
            onSecondary = LightBlue,
            onSecondaryContainer = LightBlue,
            tertiary = Color(0xFF3700B3),
            surface = White,
            surfaceContainerHigh = White,
            surfaceContainerLow = White,
            inverseSurface = backgroundColorDark,
            background = White
        )
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
