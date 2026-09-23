package com.yossibank.androidapptemplate.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.PokemonSort
import com.yossibank.androidapptemplate.R
import com.yossibank.androidapptemplate.style.badgeColor
import com.yossibank.androidapptemplate.style.labelRes
import com.yossibank.shared.PokemonTypeKind

@Composable
fun PokemonListToolbar(
    shown: Int,
    loaded: Int,
    total: Int,
    filtering: Boolean,
    sort: PokemonSort,
    onSortChange: (PokemonSort) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (filtering) {
                stringResource(R.string.pokemon_list_progress_filtered, shown, total, loaded)
            } else {
                stringResource(R.string.pokemon_list_progress, loaded, total)
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )

        SortMenu(sort = sort, onSortChange = onSortChange)
    }
}

@Composable
fun SortMenu(
    sort: PokemonSort,
    onSortChange: (PokemonSort) -> Unit,
) {
    var open by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { open = true }) {
            Text(
                text = stringResource(sort.labelRes),
                style = MaterialTheme.typography.labelMedium,
            )
        }

        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            PokemonSort.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = stringResource(option.labelRes)) },
                    onClick = {
                        onSortChange(option)
                        open = false
                    },
                )
            }
        }
    }
}

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
