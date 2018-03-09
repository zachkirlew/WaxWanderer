package com.waxwanderer.sign_up

import android.support.annotation.NonNull
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.model.User
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.google.firebase.auth.UserProfileChangeRequest


class SignUpPresenter(@NonNull private var signUpView: SignUpContract.View) : SignUpContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = SignUpActivity::class.java.simpleName

    private var dob: Date? = null

    private val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
    private val pattern = Pattern.compile(EMAIL_PATTERN)
    private lateinit var matcher : Matcher

    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = firebaseAuth.currentUser

        if (user != null) {
            Log.d(TAG, "User is Signed In")
            signUpView.startMatchDetailsActivity()
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

    override fun signUp(name: String, email: String,password: String) {

        if(!validateName(name))
            signUpView.showNameErrorMessage("Please enter a name")
        else if (!validateEmail(email)) {
            signUpView.showEmailErrorMessage("Please enter a valid email address!")
        } else if (!validatePassword(password)) {
            signUpView.showPasswordErrorMessage("Please enter a longer password!")
        }
        else {

            mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(signUpView as AppCompatActivity, { task ->
                        if (task.isSuccessful) {
                            // Sign in success
                            Log.d(TAG, "createUserWithEmail:success")
                            saveUserDetails(name, email)
                            signUpView.startMatchDetailsActivity()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                            signUpView.showCreateUserFailedMessage(task.exception?.message.toString())
                        }
                    })
        }
    }

    private fun saveUserDetails(name: String, email: String) {
        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()

        user?.updateProfile(profileUpdates)

        val updatedUser = User()

        updatedUser.name = name
        updatedUser.email = email
        updatedUser.id = user?.uid
        updatedUser.score = 0

        myRef.child("users").child(user?.uid).setValue(updatedUser)
    }

    override fun validateEmail(email: String) : Boolean {
        matcher = pattern.matcher(email);
        return matcher.matches()
    }

    override fun validateName(name: String): Boolean {
        return name.length > 1
    }

    override fun validatePassword(password: String): Boolean {
        return password.length > 5
    }

}