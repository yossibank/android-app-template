package com.yossibank.androidapptemplate.feature.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.androidapptemplate.core.screen.LiveScreen
import com.yossibank.androidapptemplate.core.screen.Screen
import com.yossibank.androidapptemplate.core.screen.ScreenActions
import com.yossibank.androidapptemplate.core.screen.text
import com.yossibank.androidapptemplate.core.screen.ui.Banner
import com.yossibank.androidapptemplate.core.screen.ui.BannerAccessory
import com.yossibank.androidapptemplate.core.screen.ui.BannerStyle
import com.yossibank.androidapptemplate.core.screen.ui.Message
import com.yossibank.androidapptemplate.core.screen.ui.Searchable
import com.yossibank.androidapptemplate.core.screen.ui.skeleton
import com.yossibank.androidapptemplate.feature.home.component.PokemonCard
import com.yossibank.androidapptemplate.feature.home.component.PokemonGrid
import com.yossibank.androidapptemplate.feature.home.component.PokemonListGauge
import com.yossibank.androidapptemplate.feature.home.component.PokemonSkeletonGrid
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    LiveScreen(viewModel) { phase, actions ->
        HomeScaffold(phase = phase, actions = actions, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScaffold(
    phase: FetchPhase<PokemonList>,
    actions: ScreenActions,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.home_title)) })
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
                onRetry = actions.reload,
                isEmpty = { it.pokemon.isEmpty() },
                loading = { PokemonSkeletonGrid() },
                empty = {
                    Message(
                        text = stringResource(R.string.home_empty_title),
                        description = stringResource(R.string.home_empty_description),
                        onRetry = actions.reload,
                    )
                },
            ) { list ->
                HomeContent(list = list, query = query, actions = actions)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    list: PokemonList,
    query: String,
    actions: ScreenActions,
) {
    val items = list.pokemon.filtered(query)
    val isFiltering = query.isNotEmpty()
    val isLoadingMore = actions.isLoadingMore && list.notice == null
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()

    if (!isFiltering && list.notice == null) {
        LaunchedEffect(gridState, items.size) {
            snapshotFlow {
                gridState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
            }.collect { lastVisible ->
                if (lastVisible != null && lastVisible >= items.size - 8) {
                    actions.loadMore()
                }
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = actions.isRefreshing,
        onRefresh = actions.refresh,
        modifier = Modifier.fillMaxSize(),
    ) {
        if (items.isEmpty()) {
            Message(
                text = stringResource(R.string.home_no_match_title, query),
                description = stringResource(R.string.home_no_match_description),
            )
            return@PullToRefreshBox
        }

        PokemonGrid(state = gridState, bottomPadding = 72.dp) {
            items(items, key = { it.id }) { entry ->
                PokemonCard(pokemon = entry)
            }

            if (isLoadingMore) {
                items(2) {
                    PokemonCard(pokemon = null, modifier = Modifier.skeleton())
                }
            }

            list.notice?.let { notice ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Banner(
                        text = notice.text(),
                        style = BannerStyle.FAILURE,
                        accessory = if (actions.isLoadingMore || actions.isRefreshing) {
                            BannerAccessory.Progress
                        } else {
                            BannerAccessory.retry { if (notice.canRetry) actions.loadMore() else actions.refresh() }
                        },
                    )
                }
            }
        }

        PokemonListGauge(
            loaded = list.pokemon.size,
            total = list.total,
            matched = if (isFiltering) items.size else null,
            onClick = { scope.launch { gridState.animateScrollToItem(0) } },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
        )
    }
}
