package io.dhruv.weatherwise

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.context = this
    }
}