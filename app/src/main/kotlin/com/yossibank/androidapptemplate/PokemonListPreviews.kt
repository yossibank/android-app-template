package com.yossibank.androidapptemplate

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yossibank.shared.PokemonBaseStat
import com.yossibank.shared.PokemonEntry
import com.yossibank.shared.PokemonEntryDetail
import com.yossibank.shared.PokemonFailure
import com.yossibank.shared.PokemonStatKind
import com.yossibank.shared.PokemonTypeKind

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
fun PokemonListLoadedPreview() {
    PreviewList(uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true))
}

@Preview(name = "読み込み中（スケルトン）", showBackground = true, heightDp = 900)
@Composable
fun PokemonListLoadingPreview() {
    PreviewList(uiState = PokemonListUiState.Loading)
}

@Preview(name = "詳細を引けなかった行", showBackground = true, heightDp = 900)
@Composable
fun PokemonListDegradedPreview() {
    PreviewList(
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
    )
}

@Preview(name = "追加取得中", showBackground = true, heightDp = 900)
@Composable
fun PokemonListLoadingMorePreview() {
    PreviewList(uiState = PokemonListUiState.Loaded(SAMPLE, hasMore = true, isLoadingMore = true))
}

@Preview(name = "空", showBackground = true)
@Composable
fun PokemonListEmptyPreview() {
    PreviewList(uiState = PokemonListUiState.Empty)
}

@Preview(name = "失敗（再試行できる）", showBackground = true)
@Composable
fun PokemonListFailedPreview() {
    PreviewList(uiState = PokemonListUiState.Failed(ErrorMessage(R.string.error_offline, canRetry = true)))
}

@Preview(name = "失敗（再試行できない）", showBackground = true)
@Composable
fun PokemonListUnrecoverablePreview() {
    PreviewList(uiState = PokemonListUiState.Failed(ErrorMessage(R.string.error_unreadable, canRetry = false)))
}

@Composable
private fun PreviewList(uiState: PokemonListUiState) {
    AppTheme {
        PokemonListScaffold(
            uiState = uiState,
            onRetry = {},
            onRetryDetails = {},
            onRefresh = {},
            onLoadMore = {},
        )
    }
}
