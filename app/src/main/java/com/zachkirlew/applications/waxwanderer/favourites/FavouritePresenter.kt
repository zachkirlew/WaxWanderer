package com.zachkirlew.applications.waxwanderer.favourites

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.google.firebase.database.DataSnapshot

class FavouritePresenter(private @NonNull var favouriteView: FavouriteContract.View) : FavouriteContract.Presenter  {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    init{
        favouriteView.setPresenter(this)
    }

    override fun start() {
        loadFavouriteVinyls()
    }

    override fun loadFavouriteVinyls() {

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("users").child(user?.uid).child("favourites")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val vinyls = ArrayList<VinylRelease>()

                for (child in dataSnapshot.children) {
                    child.getValue<VinylRelease>(VinylRelease::class.java)?.let { vinyls.add(it) }
                }

                if(vinyls.isEmpty())
                    favouriteView.showNoVinylsView()
                else
                    favouriteView.showFavouriteVinyls(vinyls)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })


    }
}