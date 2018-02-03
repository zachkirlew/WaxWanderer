package com.zachkirlew.applications.waxwanderer.matches

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.Match
import com.zachkirlew.applications.waxwanderer.data.model.User

class MatchesPresenter(private @NonNull var matchesView: MatchesContract.View) : MatchesContract.Presenter  {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    init{
        matchesView.setPresenter(this)
    }


    override fun loadMatches() {

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("matches").child(user?.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                matchesView.clearMatches()

                if(dataSnapshot.exists()){
                    dataSnapshot.children.forEach { getMatchInfo(Match(it.key,it.value as String)) }
                    matchesView.showNoMatchesView(false)
                }
                else{
                    matchesView.showNoMatchesView(true)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun getMatchInfo(match : Match){

        val myRef = database.reference

        val ref = myRef.child("users").child(match.matchedWithId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {

                    val matchedUser = dataSnapshot.getValue(User::class.java)

                    if (matchedUser != null) {
                        matchesView.addMatch(matchedUser)
                    }
                }
            }
        })
    }

    override fun deleteMatch(matchId: String) {

        val myRef = database.reference

        val userUid = mFirebaseAuth.currentUser?.uid

        //remove match from users account first
        myRef.child("matches").child(userUid)
                .child(matchId).setValue(null)

        //then remove from connections account
        myRef.child("matches").child(matchId)
                .child(userUid).setValue(null)
    }


}