package com.yossibank.androidapptemplate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yossibank.androidapptemplate.component.PokemonCard
import com.yossibank.androidapptemplate.component.PokemonDetailSheet
import com.yossibank.androidapptemplate.component.PokemonFilterBar
import com.yossibank.androidapptemplate.component.PokemonListToolbar
import com.yossibank.androidapptemplate.component.PokemonNoMatch
import com.yossibank.androidapptemplate.component.PokemonSkeletonGrid
import com.yossibank.androidapptemplate.style.GRID_ARRANGEMENT
import com.yossibank.androidapptemplate.style.GRID_COLUMNS
import com.yossibank.androidapptemplate.style.GRID_CONTENT_PADDING
import com.yossibank.androidapptemplate.ui.Banner
import com.yossibank.androidapptemplate.ui.Message
import com.yossibank.androidapptemplate.ui.Searchable
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonPager
import com.yossibank.shared.PokemonTypeKind

@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PokemonListScaffold(
        uiState = uiState,
        onRetry = viewModel::reload,
        onRetryDetails = viewModel::retryMissingDetails,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScaffold(
    uiState: PokemonListUiState,
    onRetry: () -> Unit,
    onRetryDetails: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var typeName by rememberSaveable { mutableStateOf<String?>(null) }
    var openedId by rememberSaveable { mutableStateOf<Int?>(null) }
    var sortName by rememberSaveable { mutableStateOf(PokemonSort.NUMBER.name) }

    val selectedType = typeName?.let { name -> PokemonTypeKind.entries.first { it.name == name } }
    val sort = PokemonSort.valueOf(sortName)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pokemon_list_title)) },
                actions = {
                    TextButton(onClick = onRetry) {
                        Text(text = stringResource(R.string.action_reload))
                    }
                },
            )
        },
    ) { innerPadding ->
        PokemonList(
            uiState = uiState,
            query = query,
            onQueryChange = { query = it },
            selectedType = selectedType,
            onTypeChange = { typeName = it?.name },
            sort = sort,
            onSortChange = { sortName = it.name },
            onRetry = onRetry,
            onRetryDetails = onRetryDetails,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
            onOpen = { openedId = it.id },
            modifier = Modifier.padding(innerPadding),
        )
    }

    val opened = (uiState as? PokemonListUiState.Loaded)
        ?.pokemon
        ?.firstOrNull { it.id == openedId }

    if (opened != null) {
        PokemonDetailSheet(pokemon = opened, onDismiss = { openedId = null })
    }
}

@Composable
fun PokemonList(
    uiState: PokemonListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedType: PokemonTypeKind?,
    onTypeChange: (PokemonTypeKind?) -> Unit,
    sort: PokemonSort,
    onSortChange: (PokemonSort) -> Unit,
    onRetry: () -> Unit,
    onRetryDetails: () -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onOpen: (PokemonEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        PokemonListUiState.Loading ->
            Searchable(query = query, onQueryChange = onQueryChange, modifier = modifier) {
                PokemonSkeletonGrid()
            }

        PokemonListUiState.Empty ->
            Searchable(query = query, onQueryChange = onQueryChange, modifier = modifier) {
                Message(
                    text = stringResource(R.string.pokemon_list_empty_title),
                    description = stringResource(R.string.pokemon_list_empty_description),
                    onRetry = onRetry,
                )
            }

        is PokemonListUiState.Loaded -> {
            val filtered = uiState.pokemon.filtered(query, selectedType, sort)
            val isFiltering = query.isNotEmpty() || selectedType != null

            Searchable(query = query, onQueryChange = onQueryChange, modifier = modifier) {
                PokemonListToolbar(
                    shown = filtered.size,
                    loaded = uiState.pokemon.size,
                    total = uiState.total,
                    filtering = isFiltering,
                    sort = sort,
                    onSortChange = onSortChange,
                )

                PokemonFilterBar(
                    types = uiState.pokemon.availableTypes(),
                    selected = selectedType,
                    onSelect = onTypeChange,
                )

                LoadedGrid(
                    uiState = uiState,
                    filtered = filtered,
                    isFiltering = isFiltering,
                    query = query,
                    selectedType = selectedType,
                    onLoadMore = onLoadMore,
                    onRetry = onRetry,
                    onRetryDetails = onRetryDetails,
                    onRefresh = onRefresh,
                    onOpen = onOpen,
                )
            }
        }

        is PokemonListUiState.Failed ->
            Message(
                text = uiState.error.text(),
                color = MaterialTheme.colorScheme.error,
                onRetry = onRetry.takeIf { uiState.error.canRetry },
                modifier = modifier,
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadedGrid(
    uiState: PokemonListUiState.Loaded,
    filtered: List<PokemonEntry>,
    isFiltering: Boolean,
    query: String,
    selectedType: PokemonTypeKind?,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onRetryDetails: () -> Unit,
    onRefresh: () -> Unit,
    onOpen: (PokemonEntry) -> Unit,
) {
    val gridState = rememberLazyGridState()

    if (!isFiltering && uiState.hasMore && uiState.notice == null) {
        LaunchedEffect(gridState, uiState.pokemon.size) {
            snapshotFlow {
                gridState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
            }.collect { lastVisible ->
                val threshold = uiState.pokemon.size - PokemonPager.PREFETCH_DISTANCE

                if (lastVisible != null && lastVisible >= threshold) {
                    onLoadMore()
                }
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (filtered.isEmpty()) {
            PokemonNoMatch(query = query, selectedType = selectedType)
            return@PullToRefreshBox
        }

        LazyVerticalGrid(
            columns = GRID_COLUMNS,
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = GRID_CONTENT_PADDING,
            horizontalArrangement = GRID_ARRANGEMENT,
            verticalArrangement = GRID_ARRANGEMENT,
        ) {
            if (uiState.incompleteCount > 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Banner(
                        text = stringResource(R.string.pokemon_list_incomplete, uiState.incompleteCount),
                        busy = uiState.isRepairingDetails,
                        actionRes = R.string.action_retry_details,
                        onRetry = onRetryDetails,
                    )
                }
            }

            items(filtered, key = { it.id }) { pokemon ->
                PokemonCard(pokemon = pokemon, onClick = { onOpen(pokemon) })
            }

            uiState.notice?.let { notice ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Banner(
                        text = notice.text(),
                        color = MaterialTheme.colorScheme.error,
                        onRetry = if (notice.canRetry) onLoadMore else onRetry,
                    )
                }
            }

            if (uiState.isLoadingMore) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorMessage.text(): String = stringResource(messageRes, *formatArgs.toTypedArray())
