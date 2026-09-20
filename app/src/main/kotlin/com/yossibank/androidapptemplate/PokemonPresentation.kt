package com.yossibank.androidapptemplate

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.yossibank.shared.PokemonTypeKind

@get:StringRes
val PokemonTypeKind.labelRes: Int
    get() = when (this) {
        PokemonTypeKind.NORMAL -> R.string.type_normal
        PokemonTypeKind.FIRE -> R.string.type_fire
        PokemonTypeKind.WATER -> R.string.type_water
        PokemonTypeKind.ELECTRIC -> R.string.type_electric
        PokemonTypeKind.GRASS -> R.string.type_grass
        PokemonTypeKind.ICE -> R.string.type_ice
        PokemonTypeKind.FIGHTING -> R.string.type_fighting
        PokemonTypeKind.POISON -> R.string.type_poison
        PokemonTypeKind.GROUND -> R.string.type_ground
        PokemonTypeKind.FLYING -> R.string.type_flying
        PokemonTypeKind.PSYCHIC -> R.string.type_psychic
        PokemonTypeKind.BUG -> R.string.type_bug
        PokemonTypeKind.ROCK -> R.string.type_rock
        PokemonTypeKind.GHOST -> R.string.type_ghost
        PokemonTypeKind.DRAGON -> R.string.type_dragon
        PokemonTypeKind.DARK -> R.string.type_dark
        PokemonTypeKind.STEEL -> R.string.type_steel
        PokemonTypeKind.FAIRY -> R.string.type_fairy
        PokemonTypeKind.UNKNOWN -> R.string.type_unknown
    }

val PokemonTypeKind.badgeColor: Color
    get() = when (this) {
        PokemonTypeKind.NORMAL -> Color(0xFF9FA19F)
        PokemonTypeKind.FIRE -> Color(0xFFE62829)
        PokemonTypeKind.WATER -> Color(0xFF2980EF)
        PokemonTypeKind.ELECTRIC -> Color(0xFFCFA100)
        PokemonTypeKind.GRASS -> Color(0xFF3FA129)
        PokemonTypeKind.ICE -> Color(0xFF3DCEF3)
        PokemonTypeKind.FIGHTING -> Color(0xFFFF8000)
        PokemonTypeKind.POISON -> Color(0xFF9141CB)
        PokemonTypeKind.GROUND -> Color(0xFF915121)
        PokemonTypeKind.FLYING -> Color(0xFF81B9EF)
        PokemonTypeKind.PSYCHIC -> Color(0xFFEF4179)
        PokemonTypeKind.BUG -> Color(0xFF91A119)
        PokemonTypeKind.ROCK -> Color(0xFFAFA981)
        PokemonTypeKind.GHOST -> Color(0xFF704170)
        PokemonTypeKind.DRAGON -> Color(0xFF5060E1)
        PokemonTypeKind.DARK -> Color(0xFF624D4E)
        PokemonTypeKind.STEEL -> Color(0xFF60A1B8)
        PokemonTypeKind.FAIRY -> Color(0xFFEF70EF)
        PokemonTypeKind.UNKNOWN -> Color(0xFF68A090)
    }
