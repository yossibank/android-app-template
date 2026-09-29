package com.yossibank.androidapptemplate.feature.home.component

import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.crossfade
import coil3.toBitmap
import com.yossibank.shared.artwork.ArtworkTint
import com.yossibank.shared.pokemon.PokemonEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val tints = LruCache<String, Color>(200)

fun cachedTint(imageUrl: String): Color? = tints.get(imageUrl)

@Composable
fun PokemonArtwork(
    pokemon: PokemonEntry?,
    color: Color,
    onTint: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (pokemon == null) {
            Disc(color)
        } else {
            ArtworkImage(pokemon = pokemon, color = color, onTint = onTint)
        }
    }
}

@Composable
private fun ArtworkImage(
    pokemon: PokemonEntry,
    color: Color,
    onTint: (Color) -> Unit,
) {
    var loaded by remember(pokemon.imageUrl) { mutableStateOf<Bitmap?>(null) }
    val fallback = MaterialTheme.colorScheme.primary

    LaunchedEffect(loaded) {
        val bitmap = loaded ?: return@LaunchedEffect
        val tint = withContext(Dispatchers.Default) { tintOf(bitmap) } ?: fallback

        tints.put(pokemon.imageUrl, tint)
        onTint(tint)
    }

    SubcomposeAsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(pokemon.imageUrl)
            .allowHardware(false)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        loading = { Disc(color) },
        error = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = pokemon.displayName.take(1).uppercase(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        onSuccess = { state ->
            if (tints.get(pokemon.imageUrl) == null) {
                loaded = state.result.image.toBitmap()
            }
        },
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun Disc(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f)),
    )
}

private fun tintOf(bitmap: Bitmap): Color? {
    val scaled = Bitmap.createScaledBitmap(bitmap, 32, 32, true)
    val pixels = IntArray(32 * 32)

    scaled.getPixels(pixels, 0, 32, 0, 0, 32, 32)

    return ArtworkTint.of(pixels)?.let {
        Color.hsv(
            hue = it.hue.toFloat() * 360,
            saturation = it.saturation.toFloat(),
            value = it.brightness.toFloat(),
        )
    }
}
