package com.yossibank.androidapptemplate.core.screen.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import com.yossibank.androidapptemplate.core.screen.R

@Immutable
data class AtelierPalette(
    val ground: Color,
    val ink: Color,
    val muted: Color,
    val line: Color,
    val track: Color,
    val tile: Color,
    val onTile: Color,
    val skeleton: Color,
    val mark: Color,
    val error: Color,
)

private val LightPalette = AtelierPalette(
    ground = Color(0xFFF7F4EF),
    ink = Color(0xFF1D1B18),
    muted = Color(0xFF6B655C),
    line = Color(0xFFD8D0C4),
    track = Color(0xFFE2DBCF),
    tile = Color(0xFFEDE7DD),
    onTile = Color(0xFF4A453E),
    skeleton = Color(0xFFE9E3D9),
    mark = Color(0xFFE6D9BF),
    error = Color(0xFF9A3B2E),
)

private val DarkPalette = AtelierPalette(
    ground = Color(0xFF171513),
    ink = Color(0xFFEFE9DF),
    muted = Color(0xFFA69E91),
    line = Color(0xFF3A352F),
    track = Color(0xFF2E2A25),
    tile = Color(0xFFE6DFD3),
    onTile = Color(0xFF4A453E),
    skeleton = Color(0xFF26221E),
    mark = Color(0xFF5A4B30),
    error = Color(0xFFE08A73),
)

private val LocalAtelierPalette = staticCompositionLocalOf { LightPalette }

object Atelier {
    val palette: AtelierPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAtelierPalette.current

    private val cormorant = FontFamily(
        Font(R.font.cormorant_garamond_medium, FontWeight.Medium),
        Font(R.font.cormorant_garamond_semibold, FontWeight.SemiBold),
        Font(R.font.cormorant_garamond_medium_italic, FontWeight.Medium, FontStyle.Italic),
    )

    fun serif(
        size: TextUnit,
        semibold: Boolean = false,
        italic: Boolean = false,
        tracking: TextUnit = 0.em,
    ) = TextStyle(
        fontFamily = cormorant,
        fontSize = size,
        fontWeight = if (semibold && !italic) FontWeight.SemiBold else FontWeight.Medium,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        letterSpacing = tracking,
    )

    fun mincho(
        size: TextUnit,
        bold: Boolean = false,
        tracking: TextUnit = 0.em,
    ) = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = size,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        letterSpacing = tracking,
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) DarkPalette else LightPalette
    val base = if (darkTheme) darkColorScheme() else lightColorScheme()

    CompositionLocalProvider(LocalAtelierPalette provides palette) {
        MaterialTheme(
            colorScheme = base.copy(
                primary = palette.ink,
                onPrimary = palette.ground,
                background = palette.ground,
                onBackground = palette.ink,
                surface = palette.ground,
                onSurface = palette.ink,
                onSurfaceVariant = palette.muted,
                surfaceContainer = palette.ground,
                outline = palette.line,
                outlineVariant = palette.line,
                error = palette.error,
            ),
        ) {
            CompositionLocalProvider(LocalContentColor provides palette.ink, content = content)
        }
    }
}
