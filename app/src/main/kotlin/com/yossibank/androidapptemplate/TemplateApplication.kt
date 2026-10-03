package com.yossibank.androidapptemplate

import android.app.Application
import com.yossibank.shared.auth.Session
import com.yossibank.shared.auth.configure

class TemplateApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Session.configure(this, BuildConfig.API_BASE_URL)
    }
}
