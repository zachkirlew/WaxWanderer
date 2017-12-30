package com.zachkirlew.applications.waxwanderer.explore

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ExplorePresenter(private @NonNull var vinylRepository: VinylRepository, private @NonNull var exploreView: ExploreContract.View) : ExploreContract.Presenter  {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    init{
        exploreView.setPresenter(this)
    }

    override fun start() {
        getUserVinylPreference()
    }

    override fun loadVinylReleases(preference: VinylPreference) {

        vinylRepository.getVinyls(preference)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<DiscogsResponse>{
                    override fun onNext(response: DiscogsResponse) {

                        val results = response.results
                        results?.let {exploreView.showVinylReleases(results)  }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                })



    }

    private fun getUserVinylPreference(){
        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("users").child(user?.uid).child("vinyl preferences")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val preferenceMap = dataSnapshot.children.asIterable().toList()
                val genre = preferenceMap[0].value.toString()
                val styles = preferenceMap[1].children.map { it.value } as List <String>

                loadVinylReleases(VinylPreference(genre,styles))
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun openTaskDetails() {

    }
}