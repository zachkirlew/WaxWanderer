package com.zachkirlew.applications.waxwanderer.login

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LoginContract {
    interface View {

        fun startExploreActivity()

        fun showLoginConfirmation()

        fun showFirebaseAuthenticationFailedMessage()
    }

    interface Presenter {

        fun logInWithFirebase(account: GoogleSignInAccount)
    }
}