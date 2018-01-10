package com.zachkirlew.applications.waxwanderer.matches

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        val ref = myRef.child("users").child(user?.uid).child("connections").child("matches")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){

                    dataSnapshot.children.forEach { getMatchInfo(it.key) }
                }
                else{
                    matchesView.showNoMatchesView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun getMatchInfo(key: String?){

        val myRef = database.reference

        val ref = myRef.child("users").child(key)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.exists()) {
                    val matchedUser = dataSnapshot.getValue(User::class.java)

                    if (matchedUser != null) {

                        println("hey")

                        matchesView.addMatch(matchedUser)
                    }
                }
            }
        })
    }

}