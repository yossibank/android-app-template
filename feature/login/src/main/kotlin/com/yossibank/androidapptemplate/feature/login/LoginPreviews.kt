package com.yossibank.androidapptemplate.feature.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.yossibank.androidapptemplate.core.screen.FetchFailure
import com.yossibank.androidapptemplate.core.screen.ui.AppTheme

@Preview(name = "入力", showBackground = true)
@Composable
fun LoginPreview() {
    PreviewLogin(LoginState())
}

@Preview(name = "送信中", showBackground = true)
@Composable
fun LoginSubmittingPreview() {
    PreviewLogin(LoginState(isSubmitting = true))
}

@Preview(name = "拒否された", showBackground = true)
@Composable
fun LoginRejectedPreview() {
    PreviewLogin(LoginState(failure = FetchFailure(R.string.login_rejected, canRetry = false)))
}

@Preview(name = "接続できない", showBackground = true)
@Composable
fun LoginOfflinePreview() {
    PreviewLogin(LoginState(failure = FetchFailure.offline))
}

@Composable
private fun PreviewLogin(state: LoginState) {
    AppTheme {
        LoginForm(state = state, onUsernameChange = {}, onPasswordChange = {}, onSubmit = {})
    }
}
