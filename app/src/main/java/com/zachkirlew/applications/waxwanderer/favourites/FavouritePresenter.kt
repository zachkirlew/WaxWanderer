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
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.lang.Exception

class FavouritePresenter(private @NonNull var favouriteView: FavouriteContract.View) : FavouriteContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var disposable : Disposable? = null

    init {
        favouriteView.setPresenter(this)
    }

    override fun loadFavouriteVinyls() {
        val user = mFirebaseAuth.currentUser

        getVinyls(user?.uid)
    }

    override fun loadFavouriteVinyls(userId: String) {
        getVinyls(userId)
    }

    override fun dispose() {
        disposable?.dispose()
    }

    private fun getVinyls(uid: String?) {

        val myRef = database.reference

        val ref = myRef.child("favourites").child(uid)

        InternetConnectionUtil.isInternetOn()
                .toFlowable(BackpressureStrategy.BUFFER)
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(ref) else Flowable.error(Exception("No internet connection")) }
                .toObservable()
                .subscribe(object : Observer<DataSnapshot> {
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

                })
    }
}