package com.yossibank.androidapptemplate.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.androidapptemplate.core.screen.ScreenActions
import com.yossibank.androidapptemplate.core.screen.ui.AppTheme
import com.yossibank.shared.pokemon.PokemonEntry

private val SAMPLE = PokemonList(
    pokemon = listOf(
        1 to "bulbasaur",
        4 to "charmander",
        7 to "squirtle",
        10 to "caterpie",
        25 to "pikachu",
        149 to "dragonite",
    ).map { (id, name) -> PokemonEntry(id = id, name = name, imageUrl = "") },
    total = 1351,
)

@Preview(name = "一覧", showBackground = true, heightDp = 900)
@Composable
fun HomeLoadedPreview() {
    PreviewHome(phase = FetchPhase.Loaded(SAMPLE))
}

@Preview(name = "読み込み中", showBackground = true, heightDp = 900)
@Composable
fun HomeLoadingPreview() {
    PreviewHome(phase = FetchPhase.Loading)
}

@Preview(name = "続きを読み込み中", showBackground = true, heightDp = 900)
@Composable
fun HomeLoadingMorePreview() {
    PreviewHome(phase = FetchPhase.Loaded(SAMPLE), actions = ScreenActions(isLoadingMore = true))
}

@Preview(name = "バナー付き", showBackground = true, heightDp = 900)
@Composable
fun HomeNoticePreview() {
    PreviewHome(phase = FetchPhase.Loaded(SAMPLE.copy(notice = FetchFailure.offline)))
}

@Preview(name = "空", showBackground = true)
@Composable
fun HomeEmptyPreview() {
    PreviewHome(phase = FetchPhase.Loaded(PokemonList(pokemon = emptyList(), total = 0)))
}

@Preview(name = "失敗（再試行できる）", showBackground = true)
@Composable
fun HomeFailedPreview() {
    PreviewHome(phase = FetchPhase.Failed(FetchFailure.offline))
}

@Preview(name = "失敗（再試行できない）", showBackground = true)
@Composable
fun HomeUnrecoverablePreview() {
    PreviewHome(phase = FetchPhase.Failed(FetchFailure.unreadable))
}

@Composable
private fun PreviewHome(
    phase: FetchPhase<PokemonList>,
    actions: ScreenActions = ScreenActions(),
) {
    AppTheme {
        HomeScaffold(phase = phase, actions = actions)
    }
}
