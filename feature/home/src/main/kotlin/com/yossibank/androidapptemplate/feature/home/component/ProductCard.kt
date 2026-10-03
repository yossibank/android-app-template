package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.yossibank.shared.product.ProductEntry

@Composable
fun ProductCard(
    product: ProductEntry?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            if (product != null) {
                Thumbnail(product)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (product == null) {
            Placeholder(widthFraction = 0.8f, height = 14.dp)
            Spacer(modifier = Modifier.height(6.dp))
            Placeholder(widthFraction = 0.5f, height = 14.dp)
        } else {
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                minLines = 2,
            )
        }
    }
}

@Composable
private fun Thumbnail(product: ProductEntry) {
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
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
    )
}

@Composable
private fun Placeholder(
    widthFraction: Float,
    height: Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}
