package com.zachkirlew.applications.waxwanderer.login

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LoginContract {
    interface View {

        fun startExploreActivity()

        fun showLoginConfirmation()

        fun showFirebaseAuthenticationFailedMessage()

        fun showFacebookAuthenticationFailedMessage()
    }

    interface Presenter {

        fun logInWithFirebase(account: GoogleSignInAccount)

        fun handleFacebookAccessToken(token: AccessToken)
    }
}