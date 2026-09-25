package com.yossibank.androidapptemplate.feature.home.style

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

const val SKELETON_COUNT = 8

val GRID_COLUMNS = GridCells.Fixed(2)

val GRID_CONTENT_PADDING = PaddingValues(horizontal = 12.dp, vertical = 8.dp)

val GRID_ARRANGEMENT = Arrangement.spacedBy(10.dp)

val CARD_SHAPE = RoundedCornerShape(20.dp)

val CARD_CONTENT_PADDING = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
