package com.waxwanderer.vinyl

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

class VinylPresenter(@NonNull private var vinylDataSource: VinylDataSource,
                     @NonNull private var vinylView: VinylContract.View) : VinylContract.Presenter {

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val TAG = VinylPresenter::class.java.simpleName

    private var compositeDisposable : CompositeDisposable? = null

    private var currentPage: Int = 0

    private lateinit var queryParams : HashMap<String, String>

    init {
        vinylView.setPresenter(this)
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


    override fun loadVinylRelease(releaseId: String) {

        vinylDataSource.getVinyl(releaseId)
                .doOnSubscribe{compositeDisposable?.add(it)}
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ vinylView.showQuickViewDialog(it)},
                            {error-> vinylView.showMessage(error.message) })
    }

    override fun onLoadNextPage() {
        Log.i(TAG,"Loading next page: ")
        currentPage++
        vinylView.setRefreshing(true)
        loadVinylReleases(queryParams,currentPage)
    }



    override fun refresh() {
        currentPage = 0
        vinylView.setRefreshing(true)
        vinylView.clearVinyls()
        loadVinylReleases(queryParams)
    }


    override fun addToFavourites(vinyl: VinylRelease) {

        val myRef = database.reference.child("favourites").child(mFirebaseAuth.currentUser?.uid).child(vinyl.id.toString())

        RxFirebaseDatabase.observeSingleValueEvent(myRef).toObservable()
                .map { dataSnapshot -> dataSnapshot.exists() }
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe({ isInFavourites -> if (!isInFavourites) addToFirebase(myRef,vinyl) else vinylView.showMessage("Already in your favourites") },
                        { error -> vinylView.showMessage(error.message) })
    }

    private fun addToFirebase(myRef: DatabaseReference, vinyl: VinylRelease) {
        myRef.setValue(vinyl)
        vinylView.showMessage("Added to favourites")
    }


    private val observer = object : Observer<DiscogsResponse>{
        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }

        override fun onNext(response: DiscogsResponse) {

            vinylView.setRefreshing(false)

            val page = response.pagination?.page

            val totalPages = response.pagination?.pages
            val results = response.results

            if(page == totalPages)
                vinylView.setEndOfList(true)

            if (results?.isEmpty()!!)
                vinylView.showNoVinylsView()
            else {
                vinylView.showVinylReleases(results)
                currentPage = page!!
            }
        }

        override fun onComplete() {
            Log.i(TAG,"Loading of vinyls complete")
        }

        override fun onError(e: Throwable) {
            vinylView.setRefreshing(false)
            vinylView.showMessage(e.message)

            if (currentPage > 0) {
                currentPage--
            }
        }
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}