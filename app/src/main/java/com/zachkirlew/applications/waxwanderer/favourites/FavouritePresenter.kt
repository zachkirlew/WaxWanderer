package com.zachkirlew.applications.waxwanderer.favourites

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.google.firebase.database.DataSnapshot
import com.zachkirlew.applications.waxwanderer.data.model.User

class FavouritePresenter(private @NonNull var favouriteView: FavouriteContract.View) : FavouriteContract.Presenter  {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    init{
        favouriteView.setPresenter(this)
    }

    override fun loadFavouriteVinyls() {
        val user = mFirebaseAuth.currentUser

        getVinyls(user?.uid)
    }

    override fun loadFavouriteVinyls(userId: String) {
        getVinyls(userId)
    }

    private fun getVinyls(uid : String?){

        val myRef = database.reference

        val ref = myRef.child("favourites").child(uid)

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