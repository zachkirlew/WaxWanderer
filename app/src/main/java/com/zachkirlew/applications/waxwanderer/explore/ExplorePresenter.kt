package com.zachkirlew.applications.waxwanderer.explore

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zachkirlew.applications.waxwanderer.data.VinylDataSource
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ExplorePresenter(@NonNull private var vinylDataSource: VinylDataSource, @NonNull private var exploreView: ExploreContract.View) : ExploreContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = ExplorePresenter::class.java.simpleName

    private var compositeDisposable : CompositeDisposable? = null

    private var currentPage: Int = 0

    private lateinit var queryParams : HashMap<String, String>

    init {
        exploreView.setPresenter(this)
    }

    override fun start() {
        compositeDisposable = CompositeDisposable()
    }


    override fun loadVinylReleases(queryParams: HashMap<String, String>, pageNumber: Int) {
        this.queryParams = queryParams

        vinylDataSource.getVinyls(this.queryParams,pageNumber)
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

    override fun loadVinylRelease(releaseId: String) {

        vinylDataSource.getVinyl(releaseId)
                .doOnSubscribe{compositeDisposable?.add(it)}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({exploreView.showQuickViewDialog(it)},
                            {error->exploreView.showMessage(error.message) })
    }

    override fun onLoadNextPage() {
        currentPage++
        exploreView.setRefreshing(true)
        loadVinylReleases(queryParams,currentPage)
    }



    override fun refresh() {
        currentPage = 0
        exploreView.setRefreshing(true)
        exploreView.clearVinyls()
        loadVinylReleases(queryParams)
    }


    override fun addToFavourites(vinyl: VinylRelease) {

        val myRef = database.reference.child("favourites").child(mFirebaseAuth.currentUser?.uid).child(vinyl.id.toString())

        RxFirebaseDatabase.observeSingleValueEvent(myRef).toObservable()
                .map { dataSnapshot -> dataSnapshot.exists() }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe({ isInFavourites -> if (!isInFavourites) addToFirebase(myRef,vinyl) else exploreView.showMessage("Already in your favourites") },
                        { error -> exploreView.showMessage(error.message) })
    }

    private fun addToFirebase(myRef: DatabaseReference, vinyl: VinylRelease) {
        myRef.setValue(vinyl)
        exploreView.showMessage("Added to favourites")
    }


    private val observer = object : Observer<DiscogsResponse>{
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }

        override fun onNext(response: DiscogsResponse) {

            exploreView.setRefreshing(false)

            val page = response.pagination?.page
            val results = response.results

            if (results?.isEmpty()!!)
                exploreView.showNoVinylsView()
            else {
                exploreView.showVinylReleases(results)
                currentPage = page!!
            }
        }

        override fun onComplete() {
            Log.i(TAG,"Loading of vinyls complete")
        }

        override fun onError(e: Throwable) {
            exploreView.setRefreshing(false)
            exploreView.showMessage(e.message)

            if (currentPage > 0) {
                currentPage--
            }
        }
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}