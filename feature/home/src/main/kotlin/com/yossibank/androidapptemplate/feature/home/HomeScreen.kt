package com.yossibank.androidapptemplate.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.androidapptemplate.core.screen.LiveScreen
import com.yossibank.androidapptemplate.core.screen.Screen
import com.yossibank.androidapptemplate.core.screen.ScreenActions
import com.yossibank.androidapptemplate.core.screen.text
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.core.screen.ui.Banner
import com.yossibank.androidapptemplate.core.screen.ui.BannerAccessory
import com.yossibank.androidapptemplate.core.screen.ui.Message
import com.yossibank.androidapptemplate.feature.home.component.CatalogFooter
import com.yossibank.androidapptemplate.feature.home.component.ChapterHeader
import com.yossibank.androidapptemplate.feature.home.component.HomeHeader
import com.yossibank.androidapptemplate.feature.home.component.LeadSkeleton
import com.yossibank.androidapptemplate.feature.home.component.ProductLead
import com.yossibank.androidapptemplate.feature.home.component.ProductRow
import com.yossibank.androidapptemplate.feature.home.component.ProductTile
import com.yossibank.androidapptemplate.feature.home.component.SearchField
import com.yossibank.androidapptemplate.feature.home.component.TileSkeleton
import com.yossibank.shared.product.CatalogEntry
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onSessionEnded: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
) {
    LiveScreen(viewModel, onSessionEnded = onSessionEnded) { phase, actions ->
        HomeScaffold(phase = phase, actions = actions, onLogout = onLogout, modifier = modifier)
    }
}

@Composable
fun HomeScaffold(
    phase: FetchPhase<ProductList>,
    actions: ScreenActions,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)),
    ) {
        HomeHeader(total = (phase as? FetchPhase.Loaded)?.value?.total, onLogout = onLogout)

        Screen(
            phase = phase,
            onRetry = actions.reload,
            isEmpty = { it.products.isEmpty() },
            loading = { HomeSkeleton() },
            empty = {
                Message(
                    text = stringResource(R.string.home_empty_title),
                    description = stringResource(R.string.home_empty_description),
                    onRetry = actions.reload,
                )
            },
        ) { list ->
            HomeContent(list = list, query = query, onQueryChange = { query = it }, actions = actions)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    list: ProductList,
    query: String,
    onQueryChange: (String) -> Unit,
    actions: ScreenActions,
) {
    val items = list.products.filtered(query)
    val isFiltering = query.isNotBlank()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    if (!isFiltering && list.notice == null) {
        LaunchedEffect(listState, list.products.size) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index
            }.collect { lastVisible ->
                if (lastVisible != null && lastVisible >= listState.layoutInfo.totalItemsCount - 4) {
                    actions.loadMore()
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { if (it) focusManager.clearFocus() }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(query = query, onQueryChange = onQueryChange, modifier = Modifier.padding(horizontal = 20.dp))

        if (isFiltering) {
            FilterSummary(matched = items.size, list = list)
        }

        PullToRefreshBox(
            isRefreshing = actions.isRefreshing,
            onRefresh = actions.refresh,
            modifier = Modifier.weight(1f),
        ) {
            when {
                isFiltering && items.isEmpty() -> NoMatch(query)

                isFiltering -> Results(items = items, query = query, state = listState)

                else -> Catalog(list = list, isLoadingMore = actions.isLoadingMore && list.notice == null, state = listState)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                list.notice?.let { notice ->
                    Banner(
                        text = stringResource(R.string.home_load_more_failed, notice.text()),
                        accessory = if (actions.isLoadingMore || actions.isRefreshing) {
                            BannerAccessory.Progress
                        } else {
                            BannerAccessory.retry { if (notice.canRetry) actions.loadMore() else actions.refresh() }
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .then(if (isFiltering) Modifier.windowInsetsPadding(WindowInsets.navigationBars) else Modifier),
                    )
                }

                if (!isFiltering) {
                    CatalogFooter(
                        loaded = list.products.size,
                        total = list.total,
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        modifier = Modifier
                            .background(Atelier.palette.ground)
                            .windowInsetsPadding(WindowInsets.navigationBars),
                    )
                }
            }
        }
    }
}

@Composable
private fun Catalog(
    list: ProductList,
    isLoadingMore: Boolean,
    state: LazyListState,
) {
    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(26.dp),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 120.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        list.chapters.forEach { chapter ->
            item(key = "chapter-${chapter.index}") {
                ChapterHeader(numeral = chapter.numeral, first = chapter.first, last = chapter.last)
            }

            chapter.spreads.forEach { spread ->
                item(key = spread.lead.id) {
                    ProductLead(product = spread.lead)
                }

                if (spread.pair.isNotEmpty()) {
                    item(key = "pair-${spread.lead.id}") {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            spread.pair.forEach { ProductTile(product = it, modifier = Modifier.weight(1f)) }

                            if (spread.pair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        if (isLoadingMore) {
            list.nextChapter?.let { next ->
                item(key = "chapter-loading") {
                    ChapterHeader(numeral = next.numeral, first = next.first, last = next.last, isLoading = true)
                }
            }

            item(key = "lead-loading") {
                LeadSkeleton()
            }
        }
    }
}

@Composable
private fun Results(
    items: List<CatalogEntry>,
    query: String,
    state: LazyListState,
) {
    LazyColumn(
        state = state,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items, key = { it.id }) { product ->
            ProductRow(product = product, query = query)
        }
    }
}

@Composable
private fun FilterSummary(
    matched: Int,
    list: ProductList,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 4.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        Text(
            text = stringResource(R.string.home_progress_filtered_count, matched),
            style = Atelier.mincho(15.sp, bold = true),
        )

        Text(
            text = stringResource(R.string.home_progress_filtered_detail, list.total, list.products.size),
            style = MaterialTheme.typography.bodySmall,
            color = Atelier.palette.muted,
        )
    }
}

@Composable
private fun NoMatch(query: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp),
    ) {
        Text(
            text = stringResource(R.string.home_progress_loaded, 0),
            style = Atelier.serif(64.sp, italic = true),
            color = Atelier.palette.line,
            modifier = Modifier.clearAndSetSemantics {},
        )

        Text(
            text = stringResource(R.string.home_no_match_title, query.trim()),
            style = Atelier.mincho(18.sp, bold = true),
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.home_no_match_description),
            style = MaterialTheme.typography.bodySmall,
            color = Atelier.palette.muted,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun HomeSkeleton() {
    Column(
        verticalArrangement = Arrangement.spacedBy(26.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(top = 34.dp)
                .fillMaxWidth()
                .height(1.dp)
                .background(Atelier.palette.line),
        )

        LeadSkeleton()

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TileSkeleton(modifier = Modifier.weight(1f))
            TileSkeleton(modifier = Modifier.weight(1f))
        }
    }
}
