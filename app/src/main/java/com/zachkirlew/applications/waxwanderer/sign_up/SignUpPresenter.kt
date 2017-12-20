package com.zachkirlew.applications.waxwanderer.sign_up

import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.User
import java.util.*


class SignUpPresenter(private @NonNull var signUpView: SignUpContract.View) : SignUpContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = SignUpActivity::class.java.simpleName

    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = firebaseAuth.currentUser

        if (user != null) {
            Log.d(TAG, "User is Signed In")
            signUpView.startStylesActivity()
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

    override fun signUp(name: String, email: String, dob: Date, password: String) {

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(signUpView as AppCompatActivity, { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        Log.d(TAG, "createUserWithEmail:success")
                        saveUserDetails(name, email, dob)
                        signUpView.startStylesActivity()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        signUpView.showCreateUserFailedMessage()
                    }
                })
    }

    private fun saveUserDetails(name: String, email: String, date: Date) {
        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        myRef.child("users").child(user?.uid).setValue(User(name,email,date))

    }
}