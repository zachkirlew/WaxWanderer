package com.zachkirlew.applications.waxwanderer.explore

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.VinylDataSource
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class ExplorePresenter(@NonNull private var vinylDataSource: VinylDataSource, @NonNull private var exploreView: ExploreContract.View) : ExploreContract.Presenter {
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = ExplorePresenter::class.java.simpleName

    private var compositeDisposable : CompositeDisposable? = null

    init {
        exploreView.setPresenter(this)
    }

    override fun start() {
        compositeDisposable = CompositeDisposable()
        loadVinylPreferences()
    }

    private fun loadVinylPreferences(){

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val vinylRef = myRef.child("vinylPreferences").child(user?.uid)


        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(vinylRef,{it.children.map { it.value as String }}).toObservable()   else Observable.error(Exception("No internet connection")) }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe ({loadVinylReleases(it)},
                            {error -> exploreView.showMessage(error.message)})
    }

    override fun loadVinylReleases(styles : List<String>) {

        Observable.fromIterable(styles)
                .flatMap { style -> vinylDataSource.getVinyls(style)}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    override fun searchVinylReleases(searchText: String?) {
        if (!searchText.isNullOrEmpty()) {
            vinylDataSource.searchVinyl(searchText!!)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer)
        }
    }

    private val observer = object : Observer<DiscogsResponse>{
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }

        override fun onNext(response: DiscogsResponse) {
            if (response.results?.isEmpty()!!)
                exploreView.showNoVinylsView()
            else
                exploreView.showVinylReleases(response.results!!)
        }

        override fun onComplete() {
            Log.i(TAG,"Loading of vinyls complete")
        }

        override fun onError(e: Throwable) {
            e.printStackTrace()
            exploreView.showMessage(e.message)
        }
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}