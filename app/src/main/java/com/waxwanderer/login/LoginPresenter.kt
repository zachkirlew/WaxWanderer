package com.waxwanderer.login

import android.app.Activity
import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.waxwanderer.data.model.User


class LoginPresenter(private @NonNull val loginView: LoginContract.View) : LoginContract.Presenter {

    private val TAG = LoginActivity::class.java.simpleName

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = firebaseAuth.currentUser

        if (user != null) {
            Log.d(TAG, "User is Signed In")
            checkUserPreviousSignIn()
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

        when {
            email.isEmpty() -> loginView.showEmailErrorMessage("Please enter your email")
            password.isEmpty() -> loginView.showPasswordErrorMessage("Please enter your password")
            else -> {
                Log.d(TAG, "firebaseAuthWithEmail:" + email)
                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(loginView as Activity) { task ->
                            if (task.isSuccessful) {

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success")

                                checkUserPreviousSignIn()
                            } else {
                                loginView.showLoginView()
                                loginView.hideProgressBar()
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                loginView.showMessage("Email authentication failed: ${task.exception?.message}")
                            }
                        }
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
                        loginView.showLoginView()
                        loginView.hideProgressBar()
                        Log.w(TAG, "signInWithCredential", task.exception)
                        loginView.showMessage("Google authentication failed.")
                    } else {

                        checkUserPreviousSignIn()
                    }
                }
    }

    override fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(loginView as AppCompatActivity, { task ->
                    if (task.isSuccessful) {

                        checkUserPreviousSignIn()

                    } else {
                        loginView.showLoginView()
                        loginView.hideProgressBar()
                        //If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception);
                        loginView.showMessage("Facebook authentication failed.")
                    }
                })
    }

    //check whether user exists in database and hence has entered DOB
    private fun checkUserPreviousSignIn() {
        val user = mFirebaseAuth.currentUser

        val database = FirebaseDatabase.getInstance().reference
        val ref = database.child("users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //if user has already entered DOB and other details
                if (dataSnapshot.child(user?.uid).child("gender").exists()) {

                    checkUserHasVinylPreferences()
                } else {
                    saveUserDetails()

                    loginView.startMatchDetailsActivity()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    private fun checkUserHasVinylPreferences(){

        val user = mFirebaseAuth.currentUser

        val database = FirebaseDatabase.getInstance().reference
        val ref = database.child("vinylPreferences").child(user?.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    loginView.startExploreActivity()
                } else {
                    loginView.startStylesActivity()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    private fun saveUserDetails() {
        val myRef = FirebaseDatabase.getInstance().reference

        val user = mFirebaseAuth.currentUser

        val name = user?.displayName.toString()
        val email = user?.email.toString()

        val updatedUser = User()

        updatedUser.name = name
        updatedUser.email = email
        updatedUser.id = user?.uid
        updatedUser.score = 0

        myRef.child("users").child(user?.uid).setValue(updatedUser)
    }

}