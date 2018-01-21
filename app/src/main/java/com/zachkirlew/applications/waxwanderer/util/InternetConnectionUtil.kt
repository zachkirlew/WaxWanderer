package com.zachkirlew.applications.waxwanderer.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable

object InternetConnectionUtil {

    private lateinit var application: Application

    fun init(application: Application) {
        this.application = application
    }


    fun isInternetOn(): Observable<Boolean> {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return Observable.just(activeNetworkInfo != null && activeNetworkInfo.isConnected)
    }
}