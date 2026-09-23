package com.yossibank.androidapptemplate

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.yossibank.androidapptemplate.core.standardContains
import com.yossibank.shared.PokemonBaseStat
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonEntryDetail
import com.yossibank.shared.PokemonFailure
import com.yossibank.shared.PokemonPager
import com.yossibank.shared.PokemonStatKind
import com.yossibank.shared.PokemonTypeKind

private const val MAX_TOTAL_BASE_STAT = 720f

private const val MAX_BASE_STAT = 255f

private const val SKELETON_COUNT = 8

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
private fun PokemonListScaffold(
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

    val selectedType = typeName?.let { name -> PokemonTypeKind.entries.first { it.name == name } }

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
        PokemonSheet(pokemon = opened, onDismiss = { openedId = null })
    }
}

@Composable
private fun PokemonList(
    uiState: PokemonListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    selectedType: PokemonTypeKind?,
    onTypeChange: (PokemonTypeKind?) -> Unit,
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
                SkeletonGrid()
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
                TypeFilters(
                    types = uiState.pokemon.availableTypes(),
                    selected = selectedType,
                    onSelect = onTypeChange,
                )

                LoadedGrid(
                    uiState = uiState,
                    query = query,
                    selectedType = selectedType,
                    onLoadMore = onLoadMore,
                    onRetry = onRetry,
                    onRetryDetails = onRetryDetails,
                    onRefresh = onRefresh,
                    onOpen = onOpen,
                )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadedGrid(
    uiState: PokemonListUiState.Loaded,
    query: String,
    selectedType: PokemonTypeKind?,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onRetryDetails: () -> Unit,
    onRefresh: () -> Unit,
    onOpen: (PokemonEntry) -> Unit,
) {
    val filtered = uiState.pokemon.filter { pokemon ->
        pokemon.name.standardContains(query) && pokemon.matches(selectedType)
    }

    val gridState = rememberLazyGridState()
    val isFiltering = query.isNotEmpty() || selectedType != null

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
            NoMatch(query = query, selectedType = selectedType)
            return@PullToRefreshBox
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        text = stringResource(notice.messageRes, *notice.formatArgs.toTypedArray()),
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
private fun PokemonCard(
    pokemon: PokemonEntry,
    onClick: () -> Unit,
) {
    val detail = pokemon.detail as? PokemonEntryDetail.Loaded
    val accent = detail?.types?.firstOrNull()?.badgeColor ?: MaterialTheme.colorScheme.outline

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(accent.copy(alpha = 0.28f), accent.copy(alpha = 0.06f), Color.Transparent),
                    ),
                ).padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.pokemon_list_number, pokemon.id),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Artwork(
                detail = detail,
                fallback = pokemon.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )

            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )

            val types = detail?.types.orEmpty()

            if (types.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    types.forEach { TypeBadge(it) }
                }
            }

            if (detail != null && detail.baseStats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatBar(
                        stats = detail.baseStats,
                        total = detail.totalBaseStat,
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = stringResource(R.string.pokemon_list_total, detail.totalBaseStat),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                    )
                }
            }
        }
    }
}

@Composable
private fun Artwork(
    detail: PokemonEntryDetail.Loaded?,
    fallback: String,
    modifier: Modifier = Modifier,
) {
    val large = detail?.artworkUrl ?: detail?.spriteUrl

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (large == null) {
            Text(
                text = fallback.take(1).uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            val preview = rememberAsyncImagePainter(model = detail?.spriteUrl)

            AsyncImage(
                model = large,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                placeholder = preview,
                error = preview,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun StatBar(
    stats: List<PokemonBaseStat>,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val fraction = (total / MAX_TOTAL_BASE_STAT).coerceIn(0.04f, 1f)

    Box(
        modifier = modifier
            .height(7.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .clip(CircleShape),
        ) {
            stats.forEach { stat ->
                Box(
                    modifier = Modifier
                        .weight(stat.value.coerceAtLeast(1).toFloat())
                        .fillMaxHeight()
                        .background(stat.kind.barColor),
                )
            }
        }
    }
}

@Composable
private fun TypeFilters(
    types: List<PokemonTypeKind>,
    selected: PokemonTypeKind?,
    onSelect: (PokemonTypeKind?) -> Unit,
) {
    if (types.isEmpty()) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text(text = stringResource(R.string.pokemon_list_filter_all)) },
        )

        types.forEach { type ->
            FilterChip(
                selected = selected == type,
                onClick = { onSelect(if (selected == type) null else type) },
                label = { Text(text = stringResource(type.labelRes)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = type.badgeColor,
                    selectedLabelColor = Color.White,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokemonSheet(
    pokemon: PokemonEntry,
    onDismiss: () -> Unit,
) {
    val detail = pokemon.detail as? PokemonEntryDetail.Loaded
    val accent = detail?.types?.firstOrNull()?.badgeColor ?: MaterialTheme.colorScheme.outline

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.pokemon_list_number, pokemon.id),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = pokemon.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Artwork(
                detail = detail,
                fallback = pokemon.name,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f),
            )

            val types = detail?.types.orEmpty()

            if (types.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { TypeBadge(it) }
                }
            }

            if (detail == null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.pokemon_detail_missing),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.pokemon_list_total_caption),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = stringResource(R.string.pokemon_list_total, detail.totalBaseStat),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                detail.baseStats.forEach { stat ->
                    StatRow(stat)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_close))
            }
        }
    }
}

@Composable
private fun StatRow(stat: PokemonBaseStat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(stat.kind.labelRes),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(0.24f),
        )

        Text(
            text = stat.value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth(0.14f),
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((stat.value / MAX_BASE_STAT).coerceIn(0.02f, 1f))
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(stat.kind.barColor),
            )
        }
    }
}

@Composable
private fun SkeletonGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        userScrollEnabled = false,
    ) {
        items(List(SKELETON_COUNT) { it }, key = { it }) {
            SkeletonCard()
        }
    }
}

@Composable
private fun SkeletonCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            SkeletonBlock(widthFraction = 0.3f, height = 12.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }

            SkeletonBlock(widthFraction = 0.7f, height = 16.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonBlock(widthFraction = 0.5f, height = 12.dp)
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonBlock(widthFraction = 1f, height = 7.dp)
        }
    }
}

@Composable
private fun SkeletonBlock(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}

@Composable
private fun TypeBadge(type: PokemonTypeKind) {
    Text(
        text = stringResource(type.labelRes),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        color = Color.White,
        modifier = Modifier
            .clip(CircleShape)
            .background(type.badgeColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

@Composable
private fun Banner(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    busy: Boolean = false,
    actionRes: Int = R.string.action_reload,
    onRetry: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            modifier = Modifier.weight(1f),
        )

        if (busy) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        } else if (onRetry != null) {
            TextButton(onClick = onRetry) {
                Text(text = stringResource(actionRes))
            }
        }
    }
}

@Composable
private fun NoMatch(
    query: String,
    selectedType: PokemonTypeKind?,
) {
    if (selectedType != null) {
        Message(
            text = stringResource(R.string.pokemon_list_no_type_match),
            description = stringResource(R.string.pokemon_list_no_type_match_description),
        )
    } else {
        Message(
            text = stringResource(R.string.pokemon_list_no_match_title, query),
            description = stringResource(R.string.pokemon_list_no_match_description),
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

private fun List<PokemonEntry>.availableTypes(): List<PokemonTypeKind> = asSequence()
    .mapNotNull { it.detail as? PokemonEntryDetail.Loaded }
    .flatMap { it.types }
    .distinct()
    .sortedBy { it.ordinal }
    .toList()

private fun PokemonEntry.matches(type: PokemonTypeKind?): Boolean {
    if (type == null) return true

    return (detail as? PokemonEntryDetail.Loaded)?.types?.contains(type) == true
}

private fun sample(
    id: Int,
    name: String,
    types: List<PokemonTypeKind>,
    stats: List<Int>,
) = PokemonEntry(
    id = id,
    name = name,
    detail = PokemonEntryDetail.Loaded(
        spriteUrl = null,
        artworkUrl = null,
        types = types,
        baseStats = listOf(
            PokemonStatKind.HP,
            PokemonStatKind.ATTACK,
            PokemonStatKind.DEFENSE,
            PokemonStatKind.SPECIAL_ATTACK,
            PokemonStatKind.SPECIAL_DEFENSE,
            PokemonStatKind.SPEED,
        ).zip(stats) { kind, value -> PokemonBaseStat(kind, value) },
    ),
)

private val SAMPLE = listOf(
    sample(1, "bulbasaur", listOf(PokemonTypeKind.GRASS, PokemonTypeKind.POISON), listOf(45, 49, 49, 65, 65, 45)),
    sample(4, "charmander", listOf(PokemonTypeKind.FIRE), listOf(39, 52, 43, 60, 50, 65)),
    sample(7, "squirtle", listOf(PokemonTypeKind.WATER), listOf(44, 48, 65, 50, 64, 43)),
    sample(10, "caterpie", listOf(PokemonTypeKind.BUG), listOf(45, 30, 35, 20, 20, 45)),
    sample(25, "pikachu", listOf(PokemonTypeKind.ELECTRIC), listOf(35, 55, 40, 50, 50, 90)),
    sample(149, "dragonite", listOf(PokemonTypeKind.DRAGON, PokemonTypeKind.FLYING), listOf(91, 134, 95, 100, 100, 80)),
)

@Preview(name = "一覧", showBackground = true, heightDp = 900)
@Composable
private fun PokemonListLoadedPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true),
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}

@Preview(
    name = "一覧（ダーク）",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    heightDp = 900,
)
@Composable
private fun PokemonListLoadedDarkPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true),
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "読み込み中（スケルトン）", showBackground = true, heightDp = 900)
@Composable
private fun PokemonListLoadingPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Loading,
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "詳細を引けなかった行", showBackground = true, heightDp = 900)
@Composable
private fun PokemonListDegradedPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Loaded(
                listOf(
                    PokemonEntry(
                        id = 132,
                        name = "ditto",
                        detail = PokemonEntryDetail.Missing(PokemonFailure.Server(statusCode = 500)),
                    ),
                ) + SAMPLE,
                hasMore = false,
                incompleteCount = 1,
            ),
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "追加取得中", showBackground = true, heightDp = 900)
@Composable
private fun PokemonListLoadingMorePreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true, isLoadingMore = true),
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "空", showBackground = true)
@Composable
private fun PokemonListEmptyPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Empty,
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "失敗（再試行できる）", showBackground = true)
@Composable
private fun PokemonListFailedPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Failed(R.string.error_offline, canRetry = true),
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "失敗（再試行できない）", showBackground = true)
@Composable
private fun PokemonListUnrecoverablePreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Failed(R.string.error_unreadable, canRetry = false),
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}
