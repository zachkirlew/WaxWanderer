package com.zachkirlew.applications.waxwanderer.match_details

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.MatchPreference
import com.zachkirlew.applications.waxwanderer.data.model.User
import java.text.DateFormat
import java.util.*

class MatchDetailsPresenter(private @NonNull var matchDetailsView: MatchDetailsContract.View) : MatchDetailsContract.Presenter {


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

    override fun submitDetails(userGender : String, userLocation : String?, matchGender : String, matchAge : String) {

        if(userLocation.isNullOrEmpty())
            matchDetailsView.showLocationErrorMessage("Please enter a valid location")
        else if (dob==null) {
            matchDetailsView.showDOBErrorMessage("Please enter a valid date of birth")
        }
        else{

            val myRef = database.reference

            val user = mFirebaseAuth.currentUser

            val name = user?.displayName.toString()
            val email = user?.email.toString()

            val updatedUser = User()

            updatedUser.name = name
            updatedUser.email = email
            updatedUser.dob = dob
            updatedUser.gender = userGender
            updatedUser.location = userLocation

            val matchPreference = MatchPreference()
            matchPreference.gender = matchGender
            matchPreference.ageRange = matchAge

            updatedUser.matchPreference = matchPreference

            myRef.child("users").child(user?.uid).setValue(updatedUser)

            matchDetailsView.startStylesActivity()
        }

    }


}