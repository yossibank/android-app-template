package com.yossibank.androidapptemplate

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yossibank.shared.generated.model.PokemonSummary

private const val PREFETCH_DISTANCE = 3

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
        onLoadMore = viewModel::loadMore,
        modifier = modifier,
    )
}

@Composable
private fun PokemonList(
    uiState: PokemonListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
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
                    text = stringResource(R.string.pokemon_list_empty_title),
                    description = stringResource(R.string.pokemon_list_empty_description),
                    onRetry = onRetry,
                )
            }

        is PokemonListUiState.Loaded ->
            Searchable(query = query, onQueryChange = onQueryChange, modifier = modifier) {
                LoadedList(uiState = uiState, query = query, onLoadMore = onLoadMore)
            }

        is PokemonListUiState.Failed ->
            Message(
                text = stringResource(uiState.messageRes, *uiState.formatArgs.toTypedArray()),
                color = MaterialTheme.colorScheme.error,
                onRetry = onRetry.takeIf { uiState.canRetry },
                modifier = modifier,
            )
    }
}

@Composable
private fun LoadedList(
    uiState: PokemonListUiState.Loaded,
    query: String,
    onLoadMore: () -> Unit,
) {
    val filtered = uiState.pokemon.filter { it.name.standardContains(query) }

    if (filtered.isEmpty()) {
        Message(
            text = stringResource(R.string.pokemon_list_no_match_title, query),
            description = stringResource(R.string.pokemon_list_no_match_description),
        )
        return
    }

    val listState = rememberLazyListState()

    // 絞り込み中は続きを読まない。手元にある分から選んでいる最中なので。
    if (query.isEmpty() && uiState.hasMore) {
        LaunchedEffect(listState, uiState.pokemon.size) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
            }.collect { lastVisible ->
                if (lastVisible != null && lastVisible >= uiState.pokemon.size - PREFETCH_DISTANCE) {
                    onLoadMore()
                }
            }
        }
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        items(filtered, key = { it.url }) { pokemon ->
            Text(
                text = pokemon.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            )
            HorizontalDivider()
        }

        if (uiState.isLoadingMore) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
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
            placeholder = { Text(text = stringResource(R.string.pokemon_list_search_hint)) },
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
                Text(text = stringResource(R.string.action_reload))
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
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true),
            query = "",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "一覧（ダーク）", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
private fun PokemonListLoadedDarkPreview() {
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true),
            query = "",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "追加取得中", showBackground = true)
@Composable
private fun PokemonListLoadingMorePreview() {
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true, isLoadingMore = true),
            query = "",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "絞り込みで0件", showBackground = true)
@Composable
private fun PokemonListNoMatchPreview() {
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = false),
            query = "zzzz",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "空", showBackground = true)
@Composable
private fun PokemonListEmptyPreview() {
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Empty,
            query = "",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "失敗（再試行できる）", showBackground = true)
@Composable
private fun PokemonListFailedPreview() {
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Failed(R.string.error_offline, canRetry = true),
            query = "",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "失敗（再試行できない）", showBackground = true)
@Composable
private fun PokemonListUnrecoverablePreview() {
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Failed(R.string.error_unreadable, canRetry = false),
            query = "",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "読み込み中", showBackground = true)
@Composable
private fun PokemonListLoadingPreview() {
    AppTheme {
        PokemonList(
            uiState = PokemonListUiState.Loading,
            query = "",
            onQueryChange = {},
            onRetry = {},
            onLoadMore = {},
        )
    }
}
