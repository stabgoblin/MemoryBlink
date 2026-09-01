package com.example.memoryblink.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val AntigravityColorScheme = darkColorScheme(
    primary = AgCyan,
    secondary = AgPurple,
    tertiary = AgPink,
    background = AgDarkBackground,
    surface = AgSurface,
    onPrimary = AgDarkBackground,
    onSecondary = AgText,
    onTertiary = AgText,
    onBackground = AgText,
    onSurface = AgText,
)

@Composable
fun BrainyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Force antigravity theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AntigravityColorScheme,
        content = content
    )
}

