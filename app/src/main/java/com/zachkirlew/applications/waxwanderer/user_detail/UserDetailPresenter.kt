package com.zachkirlew.applications.waxwanderer.user_detail

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


class UserDetailPresenter(private @NonNull var userDetailView: UserDetailContract.View) : UserDetailContract.Presenter  {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()


    override fun loadUserFavourites(userId: String) {

        val myRef = database.reference

        val ref = myRef.child("favourites").child(userId).limitToLast(5)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    val vinyls = dataSnapshot.children.map { it.getValue<VinylRelease>(VinylRelease::class.java)!! }
                    userDetailView.showUserFavourites(vinyls)
                }
                else{
                    userDetailView.showNoFavouritesView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun loadUserStyles(userId: String) {
        val myRef = database.reference

        val ref = myRef.child("vinylPreferences").child(userId)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val preferredStyles = dataSnapshot.children.map { it.value as String }
                val commaSeparatedStyles = android.text.TextUtils.join(", ", preferredStyles)

                userDetailView.showUserStyles(commaSeparatedStyles)
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun start() {}


}