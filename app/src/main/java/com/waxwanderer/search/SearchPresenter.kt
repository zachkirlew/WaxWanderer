package com.waxwanderer.search

import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.waxwanderer.data.VinylDataSource
import com.waxwanderer.data.model.discogs.DiscogsResponse
import com.waxwanderer.data.model.discogs.VinylRelease
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchPresenter(@NonNull private var vinylDataSource: VinylDataSource, @NonNull private var searchView: SearchContract.View) : SearchContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = SearchPresenter::class.java.simpleName

    private var compositeDisposable : CompositeDisposable? = null

    private var currentPage: Int = 0

    private lateinit var queryParams : HashMap<String, String>

    init {
        searchView.setPresenter(this)
    }

    override fun start() {
        compositeDisposable = CompositeDisposable()
    }


    override fun searchVinylReleases(queryParams: HashMap<String, String>, pageNumber: Int) {

        this.queryParams = queryParams

        vinylDataSource.getVinyls(this.queryParams,pageNumber)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)

    }

    override fun loadVinylRelease(releaseId: String) {

        vinylDataSource.getVinyl(releaseId)
                .doOnSubscribe{compositeDisposable?.add(it)}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({searchView.showQuickViewDialog(it)},
                        {error->searchView.showMessage(error.message) })
    }

    override fun onLoadNextPage() {
        currentPage++
        searchView.setRefreshing(true)
        searchVinylReleases(queryParams,currentPage)
    }



    override fun refresh() {
        currentPage = 0
        searchView.setRefreshing(true)
        searchView.clearVinyls()
        searchVinylReleases(queryParams)
    }


    override fun addToFavourites(vinyl: VinylRelease) {

        val myRef = database.reference.child("favourites").child(mFirebaseAuth.currentUser?.uid).child(vinyl.id.toString())

        RxFirebaseDatabase.observeSingleValueEvent(myRef).toObservable()
                .map { dataSnapshot -> dataSnapshot.exists() }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe({ isInFavourites -> if (!isInFavourites) addToFirebase(myRef,vinyl) else searchView.showMessage("Already in your favourites") },
                        { error -> searchView.showMessage(error.message) })
    }

    private fun addToFirebase(myRef: DatabaseReference, vinyl: VinylRelease) {
        myRef.setValue(vinyl)
        searchView.showMessage("Added to favourites")
    }


    private val observer = object : Observer<DiscogsResponse> {
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }

        override fun onNext(response: DiscogsResponse) {

            searchView.setRefreshing(false)

            val page = response.pagination?.page
            val totalPages = response.pagination?.pages
            Log.i(TAG,"Page number: " + page)
            Log.i(TAG,"Total pages: " + totalPages)
            val results = response.results

            if(page == totalPages)
                searchView.setEndOfList(true)

            if (results?.isEmpty()!!)
                searchView.showNoVinylsView()
            else {
                searchView.showVinylReleases(results)
                currentPage = page!!
            }
        }

        override fun onComplete() {
            Log.i(TAG,"Loading of vinyls complete")
        }

        override fun onError(e: Throwable) {
            searchView.setRefreshing(false)
            searchView.showMessage(e.message)

            if (currentPage > 0) {
                currentPage--
            }
        }
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}