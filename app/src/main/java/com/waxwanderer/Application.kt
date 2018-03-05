package com.waxwanderer

import android.app.Application
import com.waxwanderer.data.local.Preferences
import com.waxwanderer.util.InternetConnectionUtil

class MyCustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        Preferences.init(this)
        InternetConnectionUtil.init(this)
    }
}