package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.feature.home.R
import com.yossibank.androidapptemplate.feature.home.loadedDetail
import com.yossibank.androidapptemplate.feature.home.style.CARD_CONTENT_PADDING
import com.yossibank.androidapptemplate.feature.home.style.CARD_SHAPE
import com.yossibank.androidapptemplate.feature.home.style.accentColor
import com.yossibank.shared.pokemon.PokemonEntry

@Composable
fun PokemonCard(
    pokemon: PokemonEntry,
    onClick: () -> Unit,
) {
    val detail = pokemon.loadedDetail
    val accent = detail.accentColor

    Card(
        onClick = onClick,
        shape = CARD_SHAPE,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(
            modifier = Modifier.background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.28f), accent.copy(alpha = 0.06f), Color.Transparent),
                ),
            ),
        ) {
            Text(
                text = stringResource(R.string.pokemon_list_number_plain, pokemon.id),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = accent.copy(alpha = 0.10f),
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(horizontal = 6.dp),
            )

            Column(modifier = Modifier.padding(CARD_CONTENT_PADDING)) {
                Text(
                    text = stringResource(R.string.pokemon_list_number, pokemon.id),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                PokemonArtwork(
                    detail = detail,
                    fallback = pokemon.name,
                    accent = accent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )

                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                )

                val types = detail?.types.orEmpty()

                if (types.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        types.forEach { PokemonTypeBadge(it) }
                    }
                }

                if (detail != null && detail.baseStats.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    PokemonStatBar(total = detail.totalBaseStat, accent = accent)
                }
            }
        }
    }
}
