package com.zachkirlew.applications.waxwanderer.match_details

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.local.UserPreferences
import com.zachkirlew.applications.waxwanderer.data.model.User
import java.text.DateFormat
import java.util.*

class MatchDetailsPresenter(private @NonNull var matchDetailsView: MatchDetailsContract.View,private @NonNull val preferences: UserPreferences) : MatchDetailsContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = MatchDetailsActivity::class.java.simpleName

    private var dob: Date? = null

    override fun getFormattedDate(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal.set(year, month, day, 0, 0, 0)

        dob = cal.time

        val dfMediumUK = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        val dateFormatted = dfMediumUK.format(dob)

        matchDetailsView.showDateFormatted(dateFormatted)
    }

    override fun submitDetails(userGender : String, userLocation : String?, matchGender : String,minMatchAge : Int,maxMatchAge : Int) {

        if(userLocation.isNullOrEmpty())
            matchDetailsView.showLocationErrorMessage("Please enter a valid location")
        else if (dob==null) {
            matchDetailsView.showDOBErrorMessage("Please enter a valid date of birth")
        }
        else{

            val myRef = database.reference

            val user = mFirebaseAuth.currentUser

            preferences.minMatchAge = minMatchAge
            preferences.maxMatchAge = maxMatchAge
            preferences.matchGender = matchGender

            myRef.child("users").child(user?.uid).child("location").setValue(userLocation)
            myRef.child("users").child(user?.uid).child("gender").setValue(userGender)
            myRef.child("users").child(user?.uid).child("dob").setValue(dob)

            matchDetailsView.startStylesActivity()
        }

    }


}