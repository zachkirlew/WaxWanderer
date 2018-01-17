package com.zachkirlew.applications.waxwanderer.user_detail

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


class UserDetailPresenter(private @NonNull var userDetailView: UserDetailContract.View) : UserDetailContract.Presenter  {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()


    override fun loadUserFavourites(user: User) {

        val myRef = database.reference

        val ref = myRef.child("favourites").child(user.id)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val vinyls = ArrayList<VinylRelease>()

                for (child in dataSnapshot.children) {
                    child.getValue<VinylRelease>(VinylRelease::class.java)?.let { vinyls.add(it) }
                }

                if(vinyls.isEmpty())
                    userDetailView.showNoFavouritesView()
                else
                    userDetailView.showUserFavourites(vinyls)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun start() {}


}