package com.yossibank.androidapptemplate.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.androidapptemplate.core.screen.ScreenActions
import com.yossibank.androidapptemplate.core.screen.ui.AppTheme
import com.yossibank.shared.product.ProductEntry

private val SAMPLE = ProductList(
    products = listOf(
        "Essence Mascara Lash Princess",
        "Eyeshadow Palette with Mirror",
        "Powder Canister",
        "Red Lipstick",
        "Red Nail Polish",
        "Calvin Klein CK One",
    ).mapIndexed { index, title -> ProductEntry(id = index + 1, title = title, thumbnailUrl = "") },
    total = 194,
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
    PreviewHome(phase = FetchPhase.Loaded(ProductList(products = emptyList(), total = 0)))
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
    phase: FetchPhase<ProductList>,
    actions: ScreenActions = ScreenActions(),
) {
    AppTheme {
        HomeScaffold(phase = phase, actions = actions)
    }
}
