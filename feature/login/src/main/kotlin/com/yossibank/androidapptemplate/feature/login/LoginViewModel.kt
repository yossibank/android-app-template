package com.yossibank.androidapptemplate.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.toFetchFailure
import com.yossibank.shared.auth.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val username: String = "emilys",
    val password: String = "emilyspass",
    val isSubmitting: Boolean = false,
    val failure: FetchFailure? = null,
    val loggedIn: Boolean = false,
) {
    val canSubmit: Boolean
        get() = username.isNotBlank() && password.isNotEmpty() && !isSubmitting
}

class LoginViewModel(
    private val authenticator: Authenticator = SessionAuthenticator(),
) : ViewModel() {
    private val mutableState = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = mutableState.asStateFlow()

    fun updateUsername(username: String) {
        mutableState.update { it.copy(username = username, failure = null) }
    }

    fun updatePassword(password: String) {
        mutableState.update { it.copy(password = password, failure = null) }
    }

    fun submit() {
        val current = mutableState.value

        if (!current.canSubmit) return

        mutableState.update { it.copy(isSubmitting = true, failure = null) }

        viewModelScope.launch {
            val result = authenticator.login(current.username.trim(), current.password)

            mutableState.update {
                when (result) {
                    LoginResult.LoggedIn -> it.copy(isSubmitting = false, loggedIn = true)

                    LoginResult.Rejected -> it.copy(
                        isSubmitting = false,
                        failure = FetchFailure(R.string.login_rejected, canRetry = false),
                    )

                    is LoginResult.Failed -> it.copy(isSubmitting = false, failure = result.failure.toFetchFailure())
                }
            }
        }
    }
}
