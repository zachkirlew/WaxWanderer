package com.zachkirlew.applications.waxwanderer.login

import android.app.Activity
import android.content.Context
import android.support.annotation.NonNull
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginPresenter(private val mContext: Context) : LoginContract.Presenter {

    private val TAG = LoginActivity::class.java.simpleName

    private val mView: LoginContract.View = mContext as LoginContract.View

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = firebaseAuth.currentUser

        if (user != null) {
            Log.d(TAG, "User is Signed In")
                mView.startExploreActivity()
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

    override fun logInWithFirebase(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId())

        val credential = GoogleAuthProvider.getCredential(account.getIdToken(), null)
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(mContext as Activity) { task ->
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful())

                    //failed to login
                    if (!task.isSuccessful) {
                        Log.w(TAG, "signInWithCredential", task.exception)
                        mView.showFirebaseAuthenticationFailedMessage()
                    } else {
                        mView.startExploreActivity()
                    }
                }
    }

    companion object {

        private val TAG = LoginActivity::class.java.simpleName
    }
}