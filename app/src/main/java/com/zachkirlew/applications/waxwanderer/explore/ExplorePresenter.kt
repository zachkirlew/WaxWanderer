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
import com.zachkirlew.applications.waxwanderer.util.InternetConnectionUtil
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.intellij.lang.annotations.Flow
import java.lang.Exception

class ExplorePresenter(private @NonNull var vinylRepository: VinylRepository, private @NonNull var exploreView: ExploreContract.View) : ExploreContract.Presenter {
    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = ExplorePresenter::class.java.simpleName

    private var disposable : Disposable? = null

    init {
        exploreView.setPresenter(this)
    }

    override fun start() {
        loadStyles()
    }

    private fun loadStyles(){

        val myRef = database.reference

        val user = mFirebaseAuth.currentUser

        val vinylRef = myRef.child("vinylPreferences").child(user?.uid)

        InternetConnectionUtil.isInternetOn()
                .flatMap { isInternetOn -> if (isInternetOn) RxFirebaseDatabase.observeValueEvent(vinylRef,{it.children.map { it.value as String }}).toObservable()   else Observable.error(Exception("No internet connection")) }
                .subscribe {  loadVinylReleases(it)}
    }

    override fun loadVinylReleases(styles : List<String>) {

        Observable.fromIterable(styles)
                .flatMap { style -> vinylRepository.getVinyls(style)}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    override fun searchVinylReleases(searchText: String?) {

        if (!searchText.isNullOrEmpty()) {
            vinylRepository.searchVinyl(searchText!!)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer)
        }
    }

    private val observer = object : Observer<DiscogsResponse>{
        override fun onSubscribe(d: Disposable) {
            disposable = d
        }

        override fun onNext(response: DiscogsResponse) {

            println("on next")
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
        disposable?.dispose()
    }
}