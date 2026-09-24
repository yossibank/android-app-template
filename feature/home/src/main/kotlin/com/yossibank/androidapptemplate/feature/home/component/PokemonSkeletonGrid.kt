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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.feature.home.style.CARD_CONTENT_PADDING
import com.yossibank.androidapptemplate.feature.home.style.CARD_SHAPE
import com.yossibank.androidapptemplate.feature.home.style.GRID_ARRANGEMENT
import com.yossibank.androidapptemplate.feature.home.style.GRID_COLUMNS
import com.yossibank.androidapptemplate.feature.home.style.GRID_CONTENT_PADDING
import com.yossibank.androidapptemplate.feature.home.style.SKELETON_COUNT

@Composable
fun PokemonSkeletonGrid() {
    LazyVerticalGrid(
        columns = GRID_COLUMNS,
        modifier = Modifier.fillMaxSize(),
        contentPadding = GRID_CONTENT_PADDING,
        horizontalArrangement = GRID_ARRANGEMENT,
        verticalArrangement = GRID_ARRANGEMENT,
        userScrollEnabled = false,
    ) {
        items(List(SKELETON_COUNT) { it }, key = { it }) {
            PokemonSkeletonCard()
        }
    }
}

@Composable
fun PokemonSkeletonCard() {
    Card(
        shape = CARD_SHAPE,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(CARD_CONTENT_PADDING)) {
            PokemonSkeletonBlock(widthFraction = 0.3f, height = 12.dp)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.72f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }

            PokemonSkeletonBlock(widthFraction = 0.7f, height = 16.dp)
            Spacer(modifier = Modifier.height(8.dp))
            PokemonSkeletonBlock(widthFraction = 0.5f, height = 12.dp)
            Spacer(modifier = Modifier.height(10.dp))
            PokemonSkeletonBlock(widthFraction = 1f, height = 7.dp)
        }
    }
}

@Composable
fun PokemonSkeletonBlock(
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
