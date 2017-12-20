package com.zachkirlew.applications.waxwanderer.styles

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener




class StylesPresenter(private @NonNull var stylesView: StylesContract.View) : StylesContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = StylesActivity::class.java.simpleName

    private val mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Check if user is signed in (non-null) and update UI accordingly.
        val user = firebaseAuth.currentUser

//        if (user != null) {
//            Log.d(TAG, "User is Signed In")
//            signUpView.startExploreActivity()
//        } else {
//            Log.d(TAG, "User is Signed Out")
//        }
    }

    override fun loadGenres() {
        val database = database.reference
        val ref = database.child("genres")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val genreMap = dataSnapshot.children.asIterable()

                val genres = genreMap.map { it.key }


                stylesView.showGenres(genres)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


    }




}