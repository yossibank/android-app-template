package com.yossibank.androidapptemplate.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yossibank.androidapptemplate.core.screen.text
import com.yossibank.androidapptemplate.core.screen.ui.Atelier

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) onLoggedIn()
    }

    LoginForm(
        state = state,
        onUsernameChange = viewModel::updateUsername,
        onPasswordChange = viewModel::updatePassword,
        onSubmit = viewModel::submit,
        modifier = modifier,
    )
}

@Composable
fun LoginForm(
    state: LoginState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = Atelier.palette
    val focusManager = LocalFocusManager.current
    var showsPassword by rememberSaveable { mutableStateOf(false) }
    val submit = {
        focusManager.clearFocus()
        onSubmit()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(56.dp),
        modifier = modifier
            .fillMaxSize()
            .background(palette.ground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 28.dp, end = 28.dp, top = 96.dp, bottom = 48.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = stringResource(R.string.login_collection),
                style = Atelier.serif(40.sp, italic = true),
                color = palette.ink,
            )

            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 1.dp)
                    .background(palette.ink),
            )

            Text(
                text = stringResource(R.string.login_title),
                style = Atelier.mincho(22.sp, bold = true, tracking = 0.08.em),
                color = palette.ink,
                modifier = Modifier.semantics { heading() },
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            UnderlineField(
                label = stringResource(R.string.login_username),
                value = state.username,
                onValueChange = onUsernameChange,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            )

            UnderlineField(
                label = stringResource(R.string.login_password),
                value = state.password,
                onValueChange = onPasswordChange,
                visualTransformation = if (showsPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            ) {
                val description = stringResource(
                    if (showsPassword) R.string.login_hide_password else R.string.login_show_password,
                )

                IconButton(onClick = { showsPassword = !showsPassword }, modifier = Modifier.size(44.dp)) {
                    Icon(
                        painter = painterResource(
                            if (showsPassword) R.drawable.ic_visibility_off else R.drawable.ic_visibility,
                        ),
                        contentDescription = description,
                        tint = palette.muted,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            state.failure?.let {
                Text(text = it.text(), style = MaterialTheme.typography.bodySmall, color = palette.error)
            }

            Button(
                onClick = submit,
                enabled = state.canSubmit,
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.ink,
                    contentColor = palette.ground,
                    disabledContainerColor = palette.ink.copy(alpha = if (state.isSubmitting) 1f else 0.35f),
                    disabledContentColor = palette.ground,
                ),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        color = palette.ground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp),
                    )
                } else {
                    Text(
                        text = stringResource(R.string.login_submit),
                        style = Atelier.mincho(15.sp, bold = true, tracking = 0.24.em),
                    )
                }
            }
        }
    }
}

@Composable
private fun UnderlineField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailing: @Composable () -> Unit = {},
) {
    val palette = Atelier.palette
    var focused by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.2.em),
            color = palette.muted,
        )

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.heightIn(min = 44.dp)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = palette.ink),
                cursorBrush = SolidColor(palette.ink),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .onFocusChanged { focused = it.isFocused }
                    .semantics { contentDescription = label },
            )

            trailing()
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (focused) 1.5.dp else 1.dp)
                .background(palette.ink),
        )
    }
}
