package com.yossibank.androidapptemplate.core.screen.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.core.screen.R

enum class BannerStyle(
    @param:DrawableRes val icon: Int,
) {
    INFO(R.drawable.ic_banner_info),
    FAILURE(R.drawable.ic_banner_failure),
    ;

    val tint: Color
        @Composable get() = when (this) {
            INFO -> MaterialTheme.colorScheme.onSurfaceVariant
            FAILURE -> MaterialTheme.colorScheme.error
        }
}

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
    style: BannerStyle = BannerStyle.INFO,
    accessory: BannerAccessory = BannerAccessory.None,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false),
        ) {
            Icon(
                painter = painterResource(style.icon),
                contentDescription = null,
                tint = style.tint,
                modifier = Modifier.size(16.dp),
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = style.tint,
            )
        }

        when (accessory) {
            BannerAccessory.None -> Unit

            BannerAccessory.Progress -> CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp),
            )

            is BannerAccessory.Button -> OutlinedButton(
                onClick = accessory.onClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(accessory.label),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
