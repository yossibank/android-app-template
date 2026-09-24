package com.yossibank.androidapptemplate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.request.crossfade
import com.yossibank.shared.PokemonEntryDetail

@Composable
fun PokemonArtwork(
    detail: PokemonEntryDetail.Loaded?,
    fallback: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val imageUrl = detail?.imageUrl

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (imageUrl == null) {
            Text(
                text = fallback.take(1).uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            SubcomposeAsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                loading = { Disc(accent) },
                error = { Disc(accent) },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
fun Disc(accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp)
            .clip(CircleShape)
            .background(accent.copy(alpha = 0.12f)),
    )
}
