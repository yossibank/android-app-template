package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yossibank.androidapptemplate.core.screen.ui.skeleton

@Composable
fun ProductSkeletonGrid() {
    ProductGrid(
        modifier = Modifier.skeleton(),
        userScrollEnabled = false,
    ) {
        items(List(8) { it }, key = { it }) {
            ProductCard(product = null)
        }
    }
}
