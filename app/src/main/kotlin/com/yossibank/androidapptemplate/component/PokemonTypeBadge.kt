package com.yossibank.androidapptemplate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yossibank.androidapptemplate.style.badgeColor
import com.yossibank.androidapptemplate.style.labelRes
import com.yossibank.androidapptemplate.style.onBadgeColor
import com.yossibank.shared.PokemonTypeKind

@Composable
fun PokemonTypeBadge(type: PokemonTypeKind) {
    Text(
        text = stringResource(type.labelRes),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = type.onBadgeColor,
        modifier = Modifier
            .clip(CircleShape)
            .background(type.badgeColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}
