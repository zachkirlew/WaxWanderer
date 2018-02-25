package com.zachkirlew.applications.waxwanderer.favourites

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.google.firebase.database.DataSnapshot
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.Exception

class FavouritePresenter(@NonNull private var favouriteView: FavouriteContract.View) : FavouriteContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var disposable : Disposable? = null

    init {
        favouriteView.setPresenter(this)
    }

    override fun loadFavouriteVinyls() {
        val user = mFirebaseAuth.currentUser
        loadFavouriteVinyls(user?.uid!!)
    }

    override fun loadFavouriteVinyls(userId: String) {

        val myRef = database.reference

        val ref = myRef.child("favourites").child(userId)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(ref).toObservable() else Observable.error(Exception("No internet connection")) }
                .subscribe(observer)
    }

    override fun removeVinylFromFavourites(vinylId: Int) {
        database.reference.child("favourites").child(mFirebaseAuth.uid).child(vinylId.toString()).setValue(null)
//        favouriteView.showVinylRemoved(vinylId)
//
    }

    private val observer = object : Observer<DataSnapshot>{
        override fun onSubscribe(d: Disposable) {
            disposable = d
        }
        override fun onNext(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                val vinyls = dataSnapshot.children.map { it.getValue<VinylRelease>(VinylRelease::class.java)!! }
                favouriteView.showFavouriteVinyls(vinyls)
            } else {
                favouriteView.showMessage("No favourites to show")
            }
        }

        override fun onError(e: Throwable) {
            favouriteView.showMessage(e.message)
        }

        override fun onComplete() {
        }
    }

    override fun dispose() {
        disposable?.dispose()
    }
}