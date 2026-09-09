package com.yossibank.androidapptemplate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yossibank.shared.generated.model.PokemonSummary

@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PokemonList(
        uiState = uiState,
        onRetry = viewModel::reload,
        modifier = modifier,
    )
}

@Composable
private fun PokemonList(
    uiState: PokemonListUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        PokemonListUiState.Loading ->
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        PokemonListUiState.Empty ->
            Message(
                text = "ポケモンが見つかりませんでした",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                onRetry = onRetry,
                modifier = modifier,
            )

        is PokemonListUiState.Loaded ->
            LazyColumn(modifier = modifier.fillMaxSize()) {
                items(uiState.pokemon, key = { it.url }) { pokemon ->
                    Text(
                        text = pokemon.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    )
                    HorizontalDivider()
                }
            }

        is PokemonListUiState.Failed ->
            Message(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                onRetry = onRetry,
                modifier = modifier,
            )
    }
}

@Composable
private fun Message(
    text: String,
    color: Color,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = color)
        TextButton(onClick = onRetry) {
            Text(text = "再取得")
        }
    }
}

@Preview(name = "一覧", showBackground = true)
@Composable
private fun PokemonListLoadedPreview() {
    MaterialTheme {
        PokemonList(
            uiState = PokemonListUiState.Loaded(
                pokemon = listOf(
                    PokemonSummary("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                    PokemonSummary("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/"),
                    PokemonSummary("venusaur", "https://pokeapi.co/api/v2/pokemon/3/"),
                ),
            ),
            onRetry = {},
        )
    }
}

@Preview(name = "空", showBackground = true)
@Composable
private fun PokemonListEmptyPreview() {
    MaterialTheme {
        PokemonList(uiState = PokemonListUiState.Empty, onRetry = {})
    }
}

@Preview(name = "失敗", showBackground = true)
@Composable
private fun PokemonListFailedPreview() {
    MaterialTheme {
        PokemonList(
            uiState = PokemonListUiState.Failed("ネットワークに接続できません"),
            onRetry = {},
        )
    }
}

@Preview(name = "読み込み中", showBackground = true)
@Composable
private fun PokemonListLoadingPreview() {
    MaterialTheme {
        PokemonList(uiState = PokemonListUiState.Loading, onRetry = {})
    }
}
