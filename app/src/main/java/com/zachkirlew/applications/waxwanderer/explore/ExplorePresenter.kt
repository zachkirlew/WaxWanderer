package com.zachkirlew.applications.waxwanderer.explore

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ExplorePresenter(private @NonNull var vinylRepository: VinylRepository, private @NonNull var exploreView: ExploreContract.View) : ExploreContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    init {
        exploreView.setPresenter(this)
    }

    override fun start() {
        getUserVinylPreference()
    }

    override fun loadVinylReleases(styles: List<String>) {

        Observable.fromIterable(styles).flatMap { style -> vinylRepository.getVinyls(style) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<DiscogsResponse> {
                    override fun onNext(response: DiscogsResponse) {

                        println(response.results?.size)

                        val results = response.results
                        results?.let { exploreView.showVinylReleases(results) }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {
                        Log.v("Explore Presenter","Loading of vinyls complete")

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                })
    }

    override fun searchVinylReleases(searchText: String?) {
        if (!searchText.isNullOrEmpty()) {

            vinylRepository.searchVinyl(searchText!!)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<DiscogsResponse> {
                        override fun onNext(response: DiscogsResponse) {

                            if (response.results?.isEmpty()!!)
                                exploreView.showNoVinylsView()
                            else
                                exploreView.showVinylReleases(response.results!!)

                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onComplete() {

                        }

                        override fun onSubscribe(d: Disposable) {

                        }
                    })
        }
    }

    private fun getUserVinylPreference() {
        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val ref = myRef.child("vinylPreferences").child(user?.uid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val styles = dataSnapshot.children.map { it.value as String }
                    loadVinylReleases(styles)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun openTaskDetails() {

    }
}