package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldAccentDark,
    onPrimary = CharcoalTeak,
    primaryContainer = LightWalnut,
    onPrimaryContainer = SandStone,
    secondary = BrassGold,
    onSecondary = CharcoalTeak,
    tertiary = MutedTerracotta,
    background = CharcoalTeak,
    onBackground = SoftCream,
    surface = WarmSurfaceDark,
    onSurface = SoftCream,
    surfaceVariant = WarmCardDark,
    onSurfaceVariant = SandStone,
    outline = MutedSlate
)

private val LightColorScheme = lightColorScheme(
    primary = WalnutBrown,
    onPrimary = WarmWhite,
    primaryContainer = SandStone,
    onPrimaryContainer = WalnutBrown,
    secondary = BrassGold,
    onSecondary = WarmWhite,
    secondaryContainer = Color(0xFFF4EAD4),
    onSecondaryContainer = Color(0xFF5D400A),
    tertiary = MutedTerracotta,
    background = WarmSand,
    onBackground = CharcoalTeak,
    surface = SoftCream,
    onSurface = CharcoalTeak,
    surfaceVariant = SandStone,
    onSurfaceVariant = LightWalnut,
    outline = Color(0xFFC7BCB0)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep bespoke luxury brand colors
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun WciFurnitureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MyApplicationTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
