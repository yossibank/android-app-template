package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.core.screen.ui.skeleton

@Composable
fun LeadSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .skeleton()
            .clearAndSetSemantics {},
    ) {
        Block(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 11f))
        Block(width = 96.dp, height = 10.dp)
        Row {
            Block(width = 180.dp, height = 16.dp)
            Spacer(modifier = Modifier.weight(1f))
            Block(width = 60.dp, height = 18.dp)
        }
    }
}

@Composable
fun TileSkeleton(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .skeleton()
            .clearAndSetSemantics {},
    ) {
        Block(modifier = Modifier.fillMaxWidth().aspectRatio(1f))
        Block(width = 110.dp, height = 12.dp)
        Block(width = 50.dp, height = 14.dp)
    }
}

@Composable
private fun Block(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
) {
    Box(
        modifier = modifier
            .then(if (width != null && height != null) Modifier.size(width, height) else Modifier)
            .background(Atelier.palette.skeleton),
    )
}
