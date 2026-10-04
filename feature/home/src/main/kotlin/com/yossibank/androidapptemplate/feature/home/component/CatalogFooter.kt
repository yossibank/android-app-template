package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.feature.home.R

@Composable
fun CatalogFooter(
    loaded: Int,
    total: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = Atelier.palette
    val fraction by animateFloatAsState(
        targetValue = if (total > 0) loaded.toFloat() / total else 0f,
        label = "footer",
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(palette.ground)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(2.dp)
                .background(palette.track),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(palette.ink),
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            val style = Atelier.serif(15.sp, semibold = true).copy(fontFeatureSettings = "tnum")

            Text(text = stringResource(R.string.home_progress_loaded, loaded), style = style)

            Text(text = stringResource(R.string.home_progress_total, total), style = style, color = palette.muted)
        }
    }
}
