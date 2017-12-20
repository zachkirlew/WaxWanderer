package com.zachkirlew.applications.waxwanderer.login

import android.app.Activity
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginPresenter(private @NonNull var loginView: LoginContract.View) : LoginContract.Presenter {

    private val TAG = LoginActivity::class.java.simpleName

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = firebaseAuth.currentUser

        if (user != null) {
            Log.d(TAG, "User is Signed In")
            loginView.startExploreActivity()
        } else {
            Log.d(TAG, "User is Signed Out")
        }
    }

    fun setAuthListener() {
        mFirebaseAuth.addAuthStateListener(mAuthListener)
    }

    fun removeAuthListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthListener)
    }

    override fun logInWithEmail(email : String, password : String) {

        Log.d(TAG, "firebaseAuthWithEmail:" + email)
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginView as Activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
//                        val user = mAuth.getCurrentUser()
                        loginView.startExploreActivity()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        loginView.showMessage("Email authentication failed.")
                    }
                }
    }

    override fun logInWithFirebase(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(loginView as Activity) { task ->
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful)

                    //failed to login
                    if (!task.isSuccessful) {
                        Log.w(TAG, "signInWithCredential", task.exception)
                        loginView.showMessage("Google authentication failed.")
                    } else {
                        loginView.startExploreActivity()
                    }
                }
    }



    override fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(loginView as AppCompatActivity, { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        loginView.startExploreActivity()
                    } else {
                        //If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception);
                        loginView.showMessage("Facebook authentication failed.")
                    }
                })
    }

}