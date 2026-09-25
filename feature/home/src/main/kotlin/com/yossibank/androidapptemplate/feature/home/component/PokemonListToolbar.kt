package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.feature.home.PokemonSort
import com.yossibank.androidapptemplate.feature.home.R

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
                stringResource(R.string.home_progress_filtered, shown, total, loaded)
            } else {
                stringResource(R.string.home_progress, loaded, total)
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
