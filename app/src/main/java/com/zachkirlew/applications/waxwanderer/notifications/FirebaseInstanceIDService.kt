package com.zachkirlew.applications.waxwanderer.notifications

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences


class FirebaseInstanceIDService : FirebaseInstanceIdService() {

    private val TAG: String = FirebaseInstanceIDService::class.java.simpleName

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token: " + refreshedToken)

        saveTokenInPrefs(refreshedToken)
    }

    private fun saveTokenInPrefs(refreshedToken: String?) {
        val userPreferences = UserPreferences()

        userPreferences.pushToken = refreshedToken
    }
}


