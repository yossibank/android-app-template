package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.feature.home.R
import com.yossibank.shared.pokemon.PokemonEntry

@Composable
fun PokemonCard(
    pokemon: PokemonEntry?,
    modifier: Modifier = Modifier,
) {
    var tint by remember(pokemon?.imageUrl) { mutableStateOf(pokemon?.imageUrl?.let(::cachedTint)) }

    val color = when {
        pokemon == null -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> tint ?: Color.Gray.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .background(
                Brush.verticalGradient(listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0.06f), Color.Transparent)),
            ),
    ) {
        if (pokemon != null) {
            Text(
                text = stringResource(R.string.home_number_plain, pokemon.id),
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                color = color.copy(alpha = 0.10f),
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 8.dp),
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            if (pokemon == null) {
                Placeholder(widthFraction = 0.3f, height = 12.dp)
            } else {
                Text(
                    text = stringResource(R.string.home_number, pokemon.id),
                    style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            PokemonArtwork(
                pokemon = pokemon,
                color = color,
                onTint = { tint = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )

            if (pokemon == null) {
                Placeholder(widthFraction = 0.7f, height = 16.dp)
            } else {
                Text(
                    text = pokemon.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun Placeholder(
    widthFraction: Float,
    height: Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}
