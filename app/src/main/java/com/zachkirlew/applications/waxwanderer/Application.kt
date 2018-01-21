package com.zachkirlew.applications.waxwanderer

import android.app.Application
import com.zachkirlew.applications.waxwanderer.data.local.Preferences
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil

class MyCustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        Preferences.init(this)
        InternetConnectionUtil.init(this)
    }
}