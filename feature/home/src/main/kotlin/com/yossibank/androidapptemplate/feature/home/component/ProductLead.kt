package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.feature.home.priceText
import com.yossibank.shared.product.CatalogEntry

@Composable
fun ProductLead(
    product: CatalogEntry,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
    ) {
        Box {
            ProductThumbnail(
                product = product,
                inset = 20.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 11f),
            )

            ProductNumber(
                product = product,
                size = 12.sp,
                modifier = Modifier.padding(start = 14.dp, top = 12.dp),
            )
        }

        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                product.brand?.let { BrandText(brand = it, size = 12.sp) }

                Text(text = product.title, style = Atelier.mincho(19.sp, bold = true))
            }

            Text(text = product.priceText(), style = Atelier.serif(24.sp, semibold = true))
        }
    }
}
