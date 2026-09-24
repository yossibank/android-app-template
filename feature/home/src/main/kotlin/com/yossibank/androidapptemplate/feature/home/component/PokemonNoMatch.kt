package com.yossibank.androidapptemplate.feature.home.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yossibank.androidapptemplate.core.screen.ui.Message
import com.yossibank.androidapptemplate.feature.home.R
import com.yossibank.shared.pokemon.PokemonTypeKind

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
