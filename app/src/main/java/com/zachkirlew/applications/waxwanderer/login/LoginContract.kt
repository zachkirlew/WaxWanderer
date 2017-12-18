package com.zachkirlew.applications.waxwanderer.login

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LoginContract {
    interface View {

        fun startExploreActivity()

        fun startSignUpActivity()


        fun showMessage(message: String)
    }

    interface Presenter {

        fun logInWithEmail(email : String, password : String)

        fun logInWithFirebase(account: GoogleSignInAccount)

        fun handleFacebookAccessToken(token: AccessToken)
    }
}