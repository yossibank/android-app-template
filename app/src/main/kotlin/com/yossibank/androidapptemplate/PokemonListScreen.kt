package com.yossibank.androidapptemplate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yossibank.shared.PokemonApi
import com.yossibank.shared.PokemonListResult

@Composable
fun PokemonListScreen(modifier: Modifier = Modifier) {
    val api = remember { PokemonApi() }
    val result by produceState<PokemonListResult?>(initialValue = null, api) {
        value = api.fetchPage()
    }

    when (val current = result) {
        null ->
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        is PokemonListResult.Loaded ->
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(current.pokemon, key = { it.url }) { pokemon ->
                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    )
                    HorizontalDivider()
                }
            }

        is PokemonListResult.Failed ->
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = current.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(24.dp),
                )
            }
    }
}
