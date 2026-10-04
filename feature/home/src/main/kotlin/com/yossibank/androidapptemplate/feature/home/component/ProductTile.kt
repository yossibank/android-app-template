package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.feature.home.priceText
import com.yossibank.shared.product.CatalogEntry

@Composable
fun ProductTile(
    product: CatalogEntry,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.semantics(mergeDescendants = true) {},
    ) {
        Box {
            ProductThumbnail(
                product = product,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )

            ProductNumber(product = product, modifier = Modifier.padding(start = 10.dp, top = 9.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            product.brand?.let { BrandText(brand = it) }

            Text(text = product.title, style = Atelier.mincho(13.sp).copy(lineHeight = 20.sp))

            Text(text = product.priceText(), style = Atelier.serif(16.sp, semibold = true))
        }
    }
}
