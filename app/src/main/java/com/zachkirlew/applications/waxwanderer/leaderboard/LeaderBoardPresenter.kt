package com.zachkirlew.applications.waxwanderer.leaderboard

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.leaderboard.LeaderBoardContract

class LeaderBoardPresenter(private @NonNull var leaderboardView: LeaderBoardContract.View) : LeaderBoardContract.Presenter  {

    private val database = FirebaseDatabase.getInstance()

    init{
        leaderboardView.setPresenter(this)
    }


    override fun loadUsers() {

        val myRef = database.reference

        val ref = myRef.child("users").orderByChild("score").limitToLast(10)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    val usersSortedByScore = dataSnapshot.children.map { it.getValue(User::class.java)!! }
                    leaderboardView.showUsers(usersSortedByScore)

                    usersSortedByScore.forEach { println(it.score) }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}