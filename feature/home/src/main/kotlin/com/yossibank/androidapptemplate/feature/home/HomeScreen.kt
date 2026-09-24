package com.yossibank.androidapptemplate.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.yossibank.androidapptemplate.core.screen.FetchOperation
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.androidapptemplate.core.screen.Screen
import com.yossibank.androidapptemplate.core.screen.text
import com.yossibank.androidapptemplate.core.screen.ui.Banner
import com.yossibank.androidapptemplate.core.screen.ui.Message
import com.yossibank.androidapptemplate.core.screen.ui.Searchable
import com.yossibank.androidapptemplate.feature.home.component.PokemonCard
import com.yossibank.androidapptemplate.feature.home.component.PokemonListToolbar
import com.yossibank.androidapptemplate.feature.home.component.PokemonSkeletonGrid
import com.yossibank.androidapptemplate.feature.home.style.GRID_ARRANGEMENT
import com.yossibank.androidapptemplate.feature.home.style.GRID_COLUMNS
import com.yossibank.androidapptemplate.feature.home.style.GRID_CONTENT_PADDING
import com.yossibank.shared.pokemon.PokemonEntry

private const val PREFETCH_DISTANCE = 8

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    val phase by viewModel.fetchState.phase.collectAsStateWithLifecycle()
    val running by viewModel.fetchState.running.collectAsStateWithLifecycle()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.start()
    }

    HomeScaffold(
        phase = phase,
        running = running,
        viewState = viewState,
        onRequest = viewModel::request,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScaffold(
    phase: FetchPhase<List<PokemonEntry>>,
    running: FetchOperation?,
    viewState: HomeViewState,
    onRequest: (FetchOperation) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var sortName by rememberSaveable { mutableStateOf(PokemonSort.NUMBER.name) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.home_title)) },
                actions = {
                    TextButton(onClick = { onRequest(FetchOperation.RELOAD) }) {
                        Text(text = stringResource(R.string.home_reload))
                    }
                },
            )
        },
    ) { innerPadding ->
        Searchable(
            query = query,
            onQueryChange = { query = it },
            placeholder = stringResource(R.string.home_search_prompt),
            modifier = Modifier.padding(innerPadding),
        ) {
            Screen(
                phase = phase,
                onRetry = { onRequest(FetchOperation.RELOAD) },
                isEmpty = { it.isEmpty() },
                loading = { PokemonSkeletonGrid() },
                empty = {
                    Message(
                        text = stringResource(R.string.home_empty_title),
                        description = stringResource(R.string.home_empty_description),
                        onRetry = { onRequest(FetchOperation.RELOAD) },
                    )
                },
            ) { pokemon ->
                HomeContent(
                    pokemon = pokemon,
                    query = query,
                    sort = PokemonSort.valueOf(sortName),
                    onSortChange = { sortName = it.name },
                    running = running,
                    viewState = viewState,
                    onRequest = onRequest,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    pokemon: List<PokemonEntry>,
    query: String,
    sort: PokemonSort,
    onSortChange: (PokemonSort) -> Unit,
    running: FetchOperation?,
    viewState: HomeViewState,
    onRequest: (FetchOperation) -> Unit,
) {
    val filtered = pokemon.filtered(query, sort)
    val isFiltering = query.isNotEmpty()
    val gridState = rememberLazyGridState()

    if (!isFiltering && viewState.notice == null) {
        LaunchedEffect(gridState, pokemon.size) {
            snapshotFlow {
                gridState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
            }.collect { lastVisible ->
                if (lastVisible != null && lastVisible >= pokemon.size - PREFETCH_DISTANCE) {
                    onRequest(FetchOperation.LOAD_MORE)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PokemonListToolbar(
            shown = filtered.size,
            loaded = pokemon.size,
            total = viewState.total,
            filtering = isFiltering,
            sort = sort,
            onSortChange = onSortChange,
        )

        PullToRefreshBox(
            isRefreshing = running == FetchOperation.REFRESH,
            onRefresh = { onRequest(FetchOperation.REFRESH) },
            modifier = Modifier.fillMaxSize(),
        ) {
            if (filtered.isEmpty()) {
                Message(
                    text = stringResource(R.string.home_no_match_title, query),
                    description = stringResource(R.string.home_no_match_description),
                )
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
                items(filtered, key = { it.id }) { entry ->
                    PokemonCard(pokemon = entry)
                }

                viewState.notice?.let { notice ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Banner(
                            text = notice.text(),
                            color = MaterialTheme.colorScheme.error,
                            onRetry = {
                                onRequest(if (notice.canRetry) FetchOperation.LOAD_MORE else FetchOperation.RELOAD)
                            },
                        )
                    }
                }

                if (running == FetchOperation.LOAD_MORE) {
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
}
