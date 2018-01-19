package com.zachkirlew.applications.waxwanderer.util

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Observable

object InternetConnectionUtil {
    fun isInternetOn(context: Context): Observable<Boolean> {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return Observable.just(activeNetworkInfo != null && activeNetworkInfo.isConnected)
    }
}