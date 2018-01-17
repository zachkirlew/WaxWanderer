package com.zachkirlew.applications.waxwanderer

import android.app.Application
import com.zachkirlew.applications.waxwanderer.data.local.Preferences

class MyCustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        Preferences.init(this)
    }
}