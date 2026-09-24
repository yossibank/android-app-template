package com.yossibank.androidapptemplate.component

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.R
import com.yossibank.androidapptemplate.style.badgeColor
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonEntryDetail

@Composable
fun PokemonCard(
    pokemon: PokemonEntry,
    onClick: () -> Unit,
) {
    val detail = pokemon.detail as? PokemonEntryDetail.Loaded
    val accent = detail?.types?.firstOrNull()?.badgeColor ?: MaterialTheme.colorScheme.outline

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
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

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
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
