package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.feature.home.highlightedTitle
import com.yossibank.androidapptemplate.feature.home.priceText
import com.yossibank.shared.product.CatalogEntry

@Composable
fun ProductRow(
    product: CatalogEntry,
    query: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.semantics(mergeDescendants = true) {}) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
        ) {
            ProductThumbnail(product = product, inset = 10.dp, modifier = Modifier.size(96.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                product.brand?.let { BrandText(brand = it) }

                Text(
                    text = product.highlightedTitle(query, Atelier.palette.mark),
                    style = Atelier.mincho(15.sp),
                )

                Text(text = product.priceText(), style = Atelier.serif(18.sp, semibold = true))
            }
        }

        HorizontalDivider(thickness = 1.dp, color = Atelier.palette.line)
    }
}
