package com.zachkirlew.applications.waxwanderer.dob

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.model.User
import java.text.DateFormat
import java.util.*

class DOBPresenter(private @NonNull var dobView: DOBContract.View) : DOBContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = DOBActivity::class.java.simpleName

    override fun submitDOB(year: Int, month: Int, day: Int) {

        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal.set(year, month, day, 0, 0, 0)

        val dob = cal.time

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val firstName = getFirstName(user?.displayName.toString())
        val email = getFirstName(user?.email.toString())

        myRef.child("users").child(user?.uid).setValue(User(firstName, email, dob,null))
        dobView.startStylesActivity()
    }

    private fun getFirstName(fullName: String): String {
        return fullName.split(" ")[0]
    }


}