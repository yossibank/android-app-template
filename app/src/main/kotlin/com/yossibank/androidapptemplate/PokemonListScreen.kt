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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yossibank.shared.PokemonApi
import com.yossibank.shared.PokemonListResult
import com.yossibank.shared.generated.model.PokemonSummary

@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    load: suspend () -> PokemonListResult = { PokemonApi().fetchPage() },
) {
    val result by produceState<PokemonListResult?>(initialValue = null) { value = load() }
    PokemonList(result = result, modifier = modifier)
}

@Composable
private fun PokemonList(
    result: PokemonListResult?,
    modifier: Modifier = Modifier,
) {
    when (result) {
        null ->
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        is PokemonListResult.Loaded ->
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(result.pokemon, key = { it.url }) { pokemon ->
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
                    text = result.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(24.dp),
                )
            }
    }
}

@Preview(name = "一覧", showBackground = true)
@Composable
private fun PokemonListLoadedPreview() {
    MaterialTheme {
        PokemonList(
            result =
                PokemonListResult.Loaded(
                    pokemon =
                        listOf(
                            PokemonSummary("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                            PokemonSummary("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/"),
                            PokemonSummary("venusaur", "https://pokeapi.co/api/v2/pokemon/3/"),
                        ),
                    hasMore = true,
                ),
        )
    }
}

@Preview(name = "失敗", showBackground = true)
@Composable
private fun PokemonListFailedPreview() {
    MaterialTheme {
        PokemonList(result = PokemonListResult.Failed("ネットワークに接続できません"))
    }
}

@Preview(name = "読み込み中", showBackground = true)
@Composable
private fun PokemonListLoadingPreview() {
    MaterialTheme {
        PokemonList(result = null)
    }
}
