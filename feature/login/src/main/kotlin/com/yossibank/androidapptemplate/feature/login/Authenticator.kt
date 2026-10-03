package com.yossibank.androidapptemplate.feature.login

import com.yossibank.shared.auth.LoginResult
import com.yossibank.shared.auth.Session

interface Authenticator {
    suspend fun login(
        username: String,
        password: String,
    ): LoginResult
}

class SessionAuthenticator : Authenticator {
    override suspend fun login(
        username: String,
        password: String,
    ): LoginResult = Session.login(username, password)
}
