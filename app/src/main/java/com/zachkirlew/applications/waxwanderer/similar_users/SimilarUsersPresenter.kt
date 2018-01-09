package com.zachkirlew.applications.waxwanderer.similar_users

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import org.joda.time.LocalDate
import org.joda.time.Period
import org.joda.time.PeriodType
import java.util.*


class SimilarUsersPresenter(private @NonNull var matchView: SimilarUsersContract.View) : SimilarUsersContract.Presenter  {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    lateinit var userInfo : User

    lateinit var user: FirebaseUser

    init{
        matchView.setPresenter(this)
    }

    override fun start() {

        val myRef = database.reference

        user = mFirebaseAuth.currentUser!!

        val userRef = myRef.child("users").child(user.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                userInfo = dataSnapshot.getValue(User::class.java)!!

                loadSimilarUsers(userInfo)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun loadSimilarUsers(userInfo : User) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val users = ArrayList<User>()

                for (child in dataSnapshot.children) {
                    child.getValue<User>(User::class.java)?.let { users.add(it) }
                }

                val filterGender = userInfo.matchPreference?.gender
                val filterAge = userInfo.matchPreference?.ageRange

                val lowerAgeLimit : Int = filterAge?.split(" - ")?.get(0)?.toInt()!!
                val upperAgeLimit : Int = filterAge.split(" - ")[1].toInt()

                //filter users by age and gender

                val filteredUsers = users.filter { user.uid != it.id }
                        .filter { dobToAge(it.dob) in lowerAgeLimit..upperAgeLimit }

                when (filterGender) {
                    "Males" -> {
                        matchView.showSimilarUsers(filteredUsers.filter{it.gender=="Male"})
                    }
                    "Females" -> {
                        matchView.showSimilarUsers(filteredUsers.filter{it.gender=="Female"})
                    }
                    else -> {
                        matchView.showSimilarUsers(filteredUsers)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun dobToAge(date : Date?): Int {

        val birthDate = LocalDate(date)

        val todaysDate = LocalDate()

        return Period(birthDate, todaysDate, PeriodType.yearMonthDay()).years
    }

}