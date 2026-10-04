package com.yossibank.androidapptemplate.core.screen.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.core.screen.R

sealed interface BannerAccessory {
    data object None : BannerAccessory

    data object Progress : BannerAccessory

    data class Button(
        @param:StringRes val label: Int,
        val onClick: () -> Unit,
    ) : BannerAccessory

    companion object {
        fun retry(onClick: () -> Unit): BannerAccessory = Button(R.string.screen_retry, onClick)
    }
}

@Composable
fun Banner(
    text: String,
    modifier: Modifier = Modifier,
    accessory: BannerAccessory = BannerAccessory.None,
) {
    val palette = Atelier.palette

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.ink)
            .padding(start = 18.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = palette.ground,
            modifier = Modifier.weight(1f),
        )

        when (accessory) {
            BannerAccessory.None -> Unit

            BannerAccessory.Progress -> Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = palette.ground,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp),
                )
            }

            is BannerAccessory.Button -> OutlineButton(
                text = stringResource(accessory.label),
                onClick = accessory.onClick,
                color = palette.ground,
                horizontalPadding = 16.dp,
                minHeight = 44.dp,
            )
        }
    }
}
