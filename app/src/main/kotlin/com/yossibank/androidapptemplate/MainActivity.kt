package com.yossibank.androidapptemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yossibank.androidapptemplate.core.screen.ui.AppTheme
import com.yossibank.androidapptemplate.feature.home.PokemonListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                PokemonListScreen()
            }
        }
    }
}
