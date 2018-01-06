package com.zachkirlew.applications.waxwanderer.match

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MatchPresenter(private @NonNull var matchView: MatchContract.View) : MatchContract.Presenter  {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    init{
        matchView.setPresenter(this)
    }

    override fun start() {
    }

}