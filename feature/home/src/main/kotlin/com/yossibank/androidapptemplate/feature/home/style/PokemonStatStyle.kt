package com.yossibank.androidapptemplate.feature.home.style

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.yossibank.androidapptemplate.feature.home.R
import com.yossibank.shared.pokemon.PokemonStatKind

@get:StringRes
val PokemonStatKind.labelRes: Int
    get() = when (this) {
        PokemonStatKind.HP -> R.string.stat_hp
        PokemonStatKind.ATTACK -> R.string.stat_attack
        PokemonStatKind.DEFENSE -> R.string.stat_defense
        PokemonStatKind.SPECIAL_ATTACK -> R.string.stat_special_attack
        PokemonStatKind.SPECIAL_DEFENSE -> R.string.stat_special_defense
        PokemonStatKind.SPEED -> R.string.stat_speed
        PokemonStatKind.OTHER -> R.string.stat_other
    }

val PokemonStatKind.barColor: Color
    get() = when (this) {
        PokemonStatKind.HP -> Color(0xFF6ABE5A)
        PokemonStatKind.ATTACK -> Color(0xFFE8734A)
        PokemonStatKind.DEFENSE -> Color(0xFF4A90D9)
        PokemonStatKind.SPECIAL_ATTACK -> Color(0xFF9B6BD6)
        PokemonStatKind.SPECIAL_DEFENSE -> Color(0xFF3FB6A8)
        PokemonStatKind.SPEED -> Color(0xFFE0B03A)
        PokemonStatKind.OTHER -> Color(0xFF9E9E9E)
    }
