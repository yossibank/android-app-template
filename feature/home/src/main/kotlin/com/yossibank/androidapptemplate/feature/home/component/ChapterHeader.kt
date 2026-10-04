package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.yossibank.androidapptemplate.core.screen.ui.Atelier
import com.yossibank.androidapptemplate.feature.home.R

@Composable
fun ChapterHeader(
    numeral: String,
    first: Int,
    last: Int,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val muted = Atelier.palette.muted

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .semantics(mergeDescendants = true) { heading() },
    ) {
        Text(text = numeral, style = Atelier.serif(13.sp, semibold = true, tracking = 0.2.em), color = muted)

        HorizontalDivider(thickness = 1.dp, color = Atelier.palette.line, modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.home_chapter_range, first, last),
            style = Atelier.serif(13.sp, semibold = true, tracking = 0.1.em),
            color = muted,
        )

        if (isLoading) {
            Text(text = stringResource(R.string.home_chapter_loading), style = Atelier.mincho(11.sp), color = muted)
        }
    }
}
