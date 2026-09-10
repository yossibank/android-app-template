package com.yossibank.androidapptemplate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    var query by rememberSaveable { mutableStateOf("") }

    PokemonList(
        uiState = uiState,
        query = query,
        onQueryChange = { query = it },
        onRetry = viewModel::reload,
        modifier = modifier,
    )
}

@Composable
private fun PokemonList(
    uiState: PokemonListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        PokemonListUiState.Loading ->
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        PokemonListUiState.Empty ->
            Searchable(query = query, onQueryChange = onQueryChange, modifier = modifier) {
                Message(
                    text = "ポケモンがいません",
                    description = "取得できましたが 1 件もありませんでした",
                    onRetry = onRetry,
                )
            }

        is PokemonListUiState.Loaded ->
            Searchable(query = query, onQueryChange = onQueryChange, modifier = modifier) {
                val filtered = uiState.pokemon.filter { it.name.contains(query, ignoreCase = true) }

                if (filtered.isEmpty()) {
                    Message(
                        text = "「$query」に一致するポケモンがいません",
                        description = "綴りを確認するか、別の語で試してください",
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(filtered, key = { it.url }) { pokemon ->
                            Text(
                                text = pokemon.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                            )
                            HorizontalDivider()
                        }
                    }
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
private fun Searchable(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(text = "名前で絞り込む") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        content()
    }
}

@Composable
private fun Message(
    text: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    color: Color = Color.Unspecified,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = color)

        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (onRetry != null) {
            TextButton(onClick = onRetry) {
                Text(text = "再取得")
            }
        }
    }
}

private val SAMPLE = listOf(
    PokemonSummary("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
    PokemonSummary("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/"),
    PokemonSummary("venusaur", "https://pokeapi.co/api/v2/pokemon/3/"),
)

@Preview(name = "一覧", showBackground = true)
@Composable
private fun PokemonListLoadedPreview() {
    MaterialTheme {
        PokemonList(
            uiState = PokemonListUiState.Loaded(SAMPLE),
            query = "",
            onQueryChange = {},
            onRetry = {},
        )
    }
}

@Preview(name = "絞り込みで0件", showBackground = true)
@Composable
private fun PokemonListNoMatchPreview() {
    MaterialTheme {
        PokemonList(
            uiState = PokemonListUiState.Loaded(SAMPLE),
            query = "zzzz",
            onQueryChange = {},
            onRetry = {},
        )
    }
}

@Preview(name = "空", showBackground = true)
@Composable
private fun PokemonListEmptyPreview() {
    MaterialTheme {
        PokemonList(
            uiState = PokemonListUiState.Empty,
            query = "",
            onQueryChange = {},
            onRetry = {},
        )
    }
}

@Preview(name = "失敗", showBackground = true)
@Composable
private fun PokemonListFailedPreview() {
    MaterialTheme {
        PokemonList(
            uiState = PokemonListUiState.Failed("ネットワークに接続できません"),
            query = "",
            onQueryChange = {},
            onRetry = {},
        )
    }
}

@Preview(name = "読み込み中", showBackground = true)
@Composable
private fun PokemonListLoadingPreview() {
    MaterialTheme {
        PokemonList(
            uiState = PokemonListUiState.Loading,
            query = "",
            onQueryChange = {},
            onRetry = {},
        )
    }
}
