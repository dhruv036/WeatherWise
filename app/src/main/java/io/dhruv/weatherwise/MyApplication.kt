package io.dhruv.weatherwise

import android.app.Application
import io.dhruv.weatherwise.utils.AppContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.context = this
    }
}