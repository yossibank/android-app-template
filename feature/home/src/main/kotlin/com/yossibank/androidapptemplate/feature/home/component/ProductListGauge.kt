package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.core.screen.ui.Gauge
import com.yossibank.androidapptemplate.feature.home.R

@Composable
fun ProductListGauge(
    loaded: Int,
    total: Int,
    matched: Int?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Gauge(
        fraction = if (matched == null && loaded < total) loaded.toFloat() / total else null,
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (matched != null) {
                Text(text = stringResource(R.string.home_progress_filtered_count, matched))

                Text(
                    text = stringResource(R.string.home_progress_filtered_detail, total, loaded),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(text = stringResource(R.string.home_progress_loaded, loaded))

                Text(
                    text = stringResource(R.string.home_progress_total, total),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
