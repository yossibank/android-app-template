package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.core.screen.ui.CapsuleMeter
import com.yossibank.androidapptemplate.feature.home.R
import com.yossibank.androidapptemplate.feature.home.loadedDetail
import com.yossibank.androidapptemplate.feature.home.style.MAX_BASE_STAT
import com.yossibank.androidapptemplate.feature.home.style.accentColor
import com.yossibank.androidapptemplate.feature.home.style.barColor
import com.yossibank.androidapptemplate.feature.home.style.labelRes
import com.yossibank.shared.pokemon.PokemonBaseStat
import com.yossibank.shared.pokemon.PokemonEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailSheet(
    pokemon: PokemonEntry,
    onDismiss: () -> Unit,
) {
    val detail = pokemon.loadedDetail
    val accent = detail.accentColor

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.26f), accent.copy(alpha = 0.04f), Color.Transparent),
                    ),
                ).padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.pokemon_list_number, pokemon.id),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            PokemonArtwork(
                detail = detail,
                fallback = pokemon.name,
                accent = accent,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f),
            )

            val types = detail?.types.orEmpty()

            if (types.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { PokemonTypeBadge(it) }
                }
            }

            if (detail == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pokemon_detail_missing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.pokemon_list_total_caption),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = stringResource(R.string.pokemon_list_total, detail.totalBaseStat),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                detail.baseStats.forEach { stat ->
                    StatRow(stat)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_close))
            }
        }
    }
}

@Composable
fun StatRow(stat: PokemonBaseStat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(stat.kind.labelRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.24f),
        )

        Text(
            text = stat.value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(0.14f),
        )

        CapsuleMeter(
            fraction = (stat.value / MAX_BASE_STAT).coerceIn(0.02f, 1f),
            color = stat.kind.barColor,
            modifier = Modifier
                .weight(1f)
                .height(8.dp),
        )
    }
}
