package com.yossibank.androidapptemplate.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.yossibank.androidapptemplate.R
import com.yossibank.androidapptemplate.ui.Message
import com.yossibank.shared.PokemonTypeKind

@Composable
fun PokemonNoMatch(
    query: String,
    selectedType: PokemonTypeKind?,
) {
    if (selectedType != null) {
        Message(
            text = stringResource(R.string.pokemon_list_no_type_match),
            description = stringResource(R.string.pokemon_list_no_type_match_description),
        )
    } else {
        Message(
            text = stringResource(R.string.pokemon_list_no_match_title, query),
            description = stringResource(R.string.pokemon_list_no_match_description),
        )
    }
}
