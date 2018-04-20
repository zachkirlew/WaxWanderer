package com.waxwanderer.match_preferences

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.local.UserPreferences
import java.text.DateFormat
import java.util.*

class MatchPreferencesPresenter(@NonNull private var matchPreferencesView: MatchPreferencesContract.View, @NonNull private val preferences: UserPreferences) : MatchPreferencesContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var dob: Date? = null

    override fun getFormattedDate(year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.timeInMillis = 0
        cal.set(year, month, day, 0, 0, 0)

        dob = cal.time

        val dfMediumUK = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK)
        val dateFormatted = dfMediumUK.format(dob)

        matchPreferencesView.showDateFormatted(dateFormatted)
    }

    override fun submitDetails(userGender : String, userLocation : String?, matchGender : String,minMatchAge : Int,maxMatchAge : Int) {

        if(userLocation.isNullOrEmpty())
            matchPreferencesView.showLocationErrorMessage("Please enter a valid location")
        else if (dob==null) {
            matchPreferencesView.showDOBErrorMessage("Please enter a valid date of birth")
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

            matchPreferencesView.startStylesActivity()
        }
    }
}