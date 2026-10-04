package com.yossibank.androidapptemplate.feature.home

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.FetchPhase
import com.yossibank.androidapptemplate.core.screen.ScreenActions
import com.yossibank.androidapptemplate.core.screen.ui.AppTheme
import com.yossibank.shared.product.CatalogEntry

private val SAMPLE = ProductList(
    products = listOf(
        CatalogEntry(id = 1, title = "Essence Mascara Lash Princess", thumbnailUrl = "", brand = "Essence", price = 9.99),
        CatalogEntry(id = 2, title = "Eyeshadow Palette with Mirror", thumbnailUrl = "", brand = "Glamour Beauty", price = 19.99),
        CatalogEntry(id = 16, title = "Apple", thumbnailUrl = "", brand = null, price = 1.99),
        CatalogEntry(id = 4, title = "Red Lipstick", thumbnailUrl = "", brand = "Chic Cosmetics", price = 12.99),
        CatalogEntry(id = 5, title = "Red Nail Polish", thumbnailUrl = "", brand = "Nail Couture", price = 8.99),
        CatalogEntry(id = 6, title = "Calvin Klein CK One", thumbnailUrl = "", brand = "Calvin Klein", price = 49.99),
    ),
    total = 194,
    pageSize = 20,
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

@Preview(name = "ダーク", showBackground = true, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeDarkPreview() {
    PreviewHome(phase = FetchPhase.Loaded(SAMPLE))
}

@Preview(name = "空", showBackground = true)
@Composable
fun HomeEmptyPreview() {
    PreviewHome(phase = FetchPhase.Loaded(ProductList(products = emptyList(), total = 0, pageSize = 20)))
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
