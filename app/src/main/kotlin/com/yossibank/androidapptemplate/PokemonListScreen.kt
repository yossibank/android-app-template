package com.yossibank.androidapptemplate

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.yossibank.androidapptemplate.core.standardContains
import com.yossibank.shared.PokemonBaseStat
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonEntryDetail
import com.yossibank.shared.PokemonFailure
import com.yossibank.shared.PokemonPager
import com.yossibank.shared.PokemonStatKind
import com.yossibank.shared.PokemonTypeKind

private const val MAX_TOTAL_BASE_STAT = 720f

@Composable
fun PokemonListScreen(
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }

    PokemonListScaffold(
        uiState = uiState,
        query = query,
        onQueryChange = { query = it },
        onRetry = viewModel::reload,
        onRetryDetails = viewModel::retryMissingDetails,
        onLoadMore = viewModel::loadMore,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PokemonListScaffold(
    uiState: PokemonListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onRetryDetails: () -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            onQueryChange = onQueryChange,
            onRetry = onRetry,
            onRetryDetails = onRetryDetails,
            onLoadMore = onLoadMore,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
private fun PokemonList(
    uiState: PokemonListUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onRetryDetails: () -> Unit,
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
                LoadedList(
                    uiState = uiState,
                    query = query,
                    onLoadMore = onLoadMore,
                    onRetry = onRetry,
                    onRetryDetails = onRetryDetails,
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

@Composable
private fun LoadedList(
    uiState: PokemonListUiState.Loaded,
    query: String,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onRetryDetails: () -> Unit,
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

    if (query.isEmpty() && uiState.hasMore && uiState.notice == null) {
        LaunchedEffect(listState, uiState.pokemon.size) {
            snapshotFlow {
                listState.layoutInfo.visibleItemsInfo
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

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
    ) {
        if (uiState.incompleteCount > 0) {
            item {
                Banner(
                    text = stringResource(R.string.pokemon_list_incomplete, uiState.incompleteCount),
                    busy = uiState.isRepairingDetails,
                    actionRes = R.string.action_retry_details,
                    onRetry = onRetryDetails,
                )
            }
        }

        items(filtered, key = { it.id }) { pokemon ->
            PokemonRow(pokemon)
        }

        uiState.notice?.let { notice ->
            item {
                Banner(
                    text = stringResource(notice.messageRes, *notice.formatArgs.toTypedArray()),
                    color = MaterialTheme.colorScheme.error,
                    onRetry = if (notice.canRetry) onLoadMore else onRetry,
                )
            }
        }

        if (uiState.isLoadingMore) {
            item {
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

@Composable
private fun PokemonRow(pokemon: PokemonEntry) {
    val detail = pokemon.detail as? PokemonEntryDetail.Loaded
    val accent = detail?.types?.firstOrNull()?.badgeColor ?: MaterialTheme.colorScheme.outline

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            accent.copy(alpha = 0.22f),
                            accent.copy(alpha = 0.06f),
                            Color.Transparent,
                        ),
                    ),
                ).padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Sprite(pokemon = pokemon, accent = accent)

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.pokemon_list_number, pokemon.id),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    val types = detail?.types.orEmpty()

                    if (types.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(7.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            types.forEach { TypeBadge(it) }
                        }
                    }
                }

                if (detail != null && detail.baseStats.isNotEmpty()) {
                    Total(total = detail.totalBaseStat, accent = accent)
                }
            }

            if (detail != null && detail.baseStats.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                StatBar(stats = detail.baseStats, total = detail.totalBaseStat)
            }
        }
    }
}

@Composable
private fun Total(
    total: Int,
    accent: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.pokemon_list_total, total),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = accent,
        )

        Text(
            text = stringResource(R.string.pokemon_list_total_caption),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatBar(
    stats: List<PokemonBaseStat>,
    total: Int,
) {
    val fraction = (total / MAX_TOTAL_BASE_STAT).coerceIn(0.04f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
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
private fun Sprite(
    pokemon: PokemonEntry,
    accent: Color,
) {
    val spriteUrl = (pokemon.detail as? PokemonEntryDetail.Loaded)?.spriteUrl

    Box(
        modifier = Modifier
            .size(66.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.38f), accent.copy(alpha = 0.10f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (spriteUrl != null) {
            AsyncImage(
                model = spriteUrl,
                contentDescription = null,
                modifier = Modifier.size(58.dp),
            )
        } else {
            Text(
                text = pokemon.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TypeBadge(type: PokemonTypeKind) {
    Text(
        text = stringResource(type.labelRes),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = Color.White,
        modifier = Modifier
            .clip(CircleShape)
            .background(type.badgeColor)
            .padding(horizontal = 10.dp, vertical = 3.dp),
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
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
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "詳細を引けなかった行", showBackground = true)
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
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "詳細を取り直している", showBackground = true)
@Composable
private fun PokemonListRepairingPreview() {
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
                isRepairingDetails = true,
                incompleteCount = 1,
            ),
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
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
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "絞り込みで0件", showBackground = true)
@Composable
private fun PokemonListNoMatchPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = false),
            query = "zzzz",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
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
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
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
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
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
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
            onLoadMore = {},
        )
    }
}

@Preview(name = "読み込み中", showBackground = true)
@Composable
private fun PokemonListLoadingPreview() {
    AppTheme {
        PokemonListScaffold(
            uiState = PokemonListUiState.Loading,
            query = "",
            onQueryChange = {},
            onRetry = {},
            onRetryDetails = {},
            onLoadMore = {},
        )
    }
}
