package com.yossibank.androidapptemplate.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.R
import com.yossibank.androidapptemplate.style.MAX_TOTAL_BASE_STAT
import com.yossibank.androidapptemplate.ui.CapsuleMeter

@Composable
fun PokemonStatBar(
    total: Int,
    accent: Color,
) {
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        CapsuleMeter(
            fraction = (total / MAX_TOTAL_BASE_STAT).coerceIn(0.04f, 1f),
            color = accent,
            modifier = Modifier
                .weight(1f, fill = false)
                .widthIn(min = 48.dp)
                .height(7.dp),
        )

        Text(
            text = stringResource(R.string.pokemon_list_total, total),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
    }
}
