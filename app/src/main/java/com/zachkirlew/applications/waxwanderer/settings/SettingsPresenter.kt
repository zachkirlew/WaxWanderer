package com.zachkirlew.applications.waxwanderer.settings

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.User

class SettingsPresenter(private @NonNull var matchView: SettingsContract.View) : SettingsContract.Presenter  {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    init{
        matchView.setPresenter(this)
    }

    override fun start() {
        loadUserDetails()
    }

    override fun loadUserDetails() {

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("users").child(user?.uid)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })


    }

}