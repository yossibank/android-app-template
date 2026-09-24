package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.feature.home.R
import com.yossibank.androidapptemplate.feature.home.style.badgeColor
import com.yossibank.androidapptemplate.feature.home.style.labelRes
import com.yossibank.shared.pokemon.PokemonTypeKind

@Composable
fun PokemonFilterBar(
    types: List<PokemonTypeKind>,
    selected: PokemonTypeKind?,
    onSelect: (PokemonTypeKind?) -> Unit,
) {
    if (types.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text(text = stringResource(R.string.pokemon_list_filter_all)) },
        )

        types.forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(if (selected == type) null else type) },
                label = { Text(text = stringResource(type.labelRes)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = type.badgeColor,
                    selectedLabelColor = Color.White,
                ),
            )
        }
    }
}
