package com.waxwanderer.login

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface LoginContract {
    interface View {

        fun startExploreActivity()

        fun startSignUpActivity()

        fun startStylesActivity()

        fun startMatchDetailsActivity()

        fun showPasswordErrorMessage(message : String)

        fun showEmailErrorMessage(message : String)

        fun showMessage(message: String)

        fun showProgressBar()

        fun hideProgressBar()

        fun showLoginView()

        fun hideLoginView()
    }

    interface Presenter {

        fun logInWithEmail(email : String, password : String)

        fun logInWithFirebase(account: GoogleSignInAccount)

        fun handleFacebookAccessToken(token: AccessToken)
    }
}