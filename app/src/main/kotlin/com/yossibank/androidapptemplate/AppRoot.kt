package com.yossibank.androidapptemplate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import com.yossibank.androidapptemplate.feature.home.HomeScreen
import com.yossibank.androidapptemplate.feature.login.LoginScreen
import com.yossibank.shared.auth.Session

@Composable
fun AppRoot() {
    var loggedIn by remember { mutableStateOf(Session.isLoggedIn) }

    if (loggedIn) {
        Scoped {
            HomeScreen(
                onLogout = {
                    Session.logout()
                    loggedIn = false
                },
                onSessionEnded = { loggedIn = false },
            )
        }
    } else {
        Scoped {
            LoginScreen(onLoggedIn = { loggedIn = true })
        }
    }
}

@Composable
private fun Scoped(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalViewModelStoreOwner provides rememberViewModelStoreOwner(), content = content)
}
