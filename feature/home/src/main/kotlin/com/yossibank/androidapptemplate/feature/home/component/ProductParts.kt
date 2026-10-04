package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.feature.home.R
import com.yossibank.androidapptemplate.feature.home.paddedId
import com.yossibank.shared.product.CatalogEntry

@Composable
fun ProductThumbnail(
    product: CatalogEntry,
    modifier: Modifier = Modifier,
    inset: Dp = 16.dp,
) {
    Box(modifier = modifier.background(Atelier.palette.tile), contentAlignment = Alignment.Center) {
        SubcomposeAsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(product.thumbnailUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            error = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = product.title.take(1).uppercase(),
                        style = Atelier.serif(40.sp, semibold = true),
                        color = Atelier.palette.onTile,
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(inset),
        )
    }
}

@Composable
fun ProductNumber(
    product: CatalogEntry,
    modifier: Modifier = Modifier,
    size: TextUnit = 11.sp,
) {
    Text(
        text = stringResource(R.string.home_product_number, product.paddedId),
        style = Atelier.serif(size, semibold = true, tracking = 0.18.em),
        color = Atelier.palette.onTile,
        modifier = modifier,
    )
}

@Composable
fun BrandText(
    brand: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 11.sp,
) {
    Text(
        text = brand.uppercase(),
        style = Atelier.serif(size, semibold = true, tracking = 0.18.em),
        color = Atelier.palette.muted,
        modifier = modifier,
    )
}
