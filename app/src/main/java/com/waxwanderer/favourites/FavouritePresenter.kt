package com.waxwanderer.favourites

import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.waxwanderer.data.model.discogs.VinylRelease
import com.google.firebase.database.DataSnapshot
import com.waxwanderer.data.VinylDataSource
import com.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception


class FavouritePresenter(@NonNull private var favouriteView: FavouriteContract.View,
                         @NonNull private var vinylDataSource: VinylDataSource) : FavouriteContract.Presenter {


    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private var compositeDisposable : CompositeDisposable? = null

    init {
        favouriteView.setPresenter(this)
    }

    override fun start() {
        compositeDisposable = CompositeDisposable()
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
    }

    private val observer = object : Observer<DataSnapshot>{
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
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

    override fun loadVinylRelease(releaseId: String) {

        InternetConnectionUtil.isInternetOn()
                .flatMapSingle { isInternetOn -> if (isInternetOn) vinylDataSource.getVinyl(releaseId) else Single.error(Exception("No internet connection")) }
                .doOnSubscribe{compositeDisposable?.add(it)}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({favouriteView.showQuickViewDialog(it)},
                        {error->favouriteView.showMessage(error.message) })
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}