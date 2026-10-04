package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.feature.home.R

@Composable
fun HomeHeader(
    total: Int?,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = Atelier.palette
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 14.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_collection),
                style = Atelier.serif(17.sp, italic = true),
                color = palette.muted,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Bottom) {
                Text(
                    text = stringResource(R.string.home_title),
                    style = Atelier.mincho(30.sp, bold = true, tracking = 0.06.em),
                    modifier = Modifier.semantics { heading() },
                )

                if (total != null) {
                    Text(
                        text = stringResource(R.string.home_progress_loaded, total),
                        style = Atelier.serif(16.sp, semibold = true),
                        color = palette.muted,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
        }

        Box {
            OutlinedIconButton(
                onClick = { menuOpen = true },
                shape = CircleShape,
                border = BorderStroke(1.dp, palette.line),
                modifier = Modifier.size(44.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_person),
                    contentDescription = stringResource(R.string.home_account),
                    tint = palette.ink,
                    modifier = Modifier.size(20.dp),
                )
            }

            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(R.string.home_logout), color = palette.error) },
                    onClick = {
                        menuOpen = false
                        onLogout()
                    },
                )
            }
        }
    }
}
