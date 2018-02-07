package com.zachkirlew.applications.waxwanderer.vinyl_detail


import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class VinylDetailPresenter(private @NonNull var vinylRepository: VinylRepository,
                           private @NonNull var vinylDetailView: VinylDetailContract.View,
                           private @NonNull var recommender: RecommenderImp) : VinylDetailContract.Presenter {

    private val TAG = VinylDetailActivity::class.java.simpleName

    private val user = FirebaseAuth.getInstance().currentUser
    private val database = FirebaseDatabase.getInstance()

    private val compositeDisposable : CompositeDisposable? = null

    override fun checkInFavourites(releaseId: String) {

        val myRef = database.reference.child("favourites").child(user?.uid).child(releaseId)

        RxFirebaseDatabase.observeValueEvent(myRef).toObservable()
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe({ setButtonColor(it) },
                            { error -> vinylDetailView.showMessage(error.message) })
    }

    private fun setButtonColor(dataSnapshot: DataSnapshot){

        if (dataSnapshot.exists()) {
            vinylDetailView.editButtonColor(true)
        } else {
            vinylDetailView.editButtonColor(false)
        }
    }

    override fun loadVinylRelease(releaseId: String) {

        vinylRepository.getVinyl(releaseId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<DetailVinylRelease> {
                    override fun onSuccess(detailVinylRelease: DetailVinylRelease) {

                        detailVinylRelease.images?.get(0)?.uri?.let { vinylDetailView.showImageBackDrop(detailVinylRelease.images?.get(0)?.uri!!) }

                        vinylDetailView.showDetailVinylInfo(detailVinylRelease)

                        detailVinylRelease.tracklist?.let { vinylDetailView.showTrackList(detailVinylRelease.tracklist) }

                        detailVinylRelease.videos?.let { vinylDetailView.showVideos(detailVinylRelease.videos) }

                        vinylDetailView.showRating(detailVinylRelease.community?.rating?.average!!)
                    }

                    override fun onError(e: Throwable) {
                        vinylDetailView.showMessage(e.message)

                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }
                })
    }

    override fun addToFavourites(vinylRelease: VinylRelease) {

        val favouriteRef = database.reference.child("favourites").child(user?.uid).child(vinylRelease.id.toString())

        RxFirebaseDatabase.observeSingleValueEvent(favouriteRef).toObservable()
                .doOnSubscribe { compositeDisposable?.add(it) }
                .subscribe({ handleFavouriteLogic(it,favouriteRef,vinylRelease) },
                            {error-> vinylDetailView.showMessage(error.message)})
    }

    private fun handleFavouriteLogic(dataSnapshot: DataSnapshot,favouriteRef : DatabaseReference,vinylRelease: VinylRelease){

        if (dataSnapshot.exists()) {

            favouriteRef.setValue(null)

            vinylDetailView.showMessage("Removed from favourites")
            removeFavouriteFromRecommender(user?.uid!!,vinylRelease.id.toString())

            vinylDetailView.addRemovedResult(true)

        } else {

            favouriteRef.setValue(vinylRelease)

            vinylDetailView.showMessage("Successfully added to favourites")
            addFavouriteToRecommender(user?.uid!!,vinylRelease.id.toString())
            vinylDetailView.addRemovedResult(false)
        }
    }

    override fun addFavouriteToRecommender(userId : String, itemId : String) {
        recommender.addFavourite(userId,itemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommenderObserver)
    }

    override fun removeFavouriteFromRecommender(userId : String, itemId : String) {
        recommender.removeFavourite(userId,itemId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommenderObserver)
    }

    private val  recommenderObserver = object : SingleObserver<String>{
        override fun onSuccess(responseString: String) {
            Log.i("VinylDetailPres",responseString)
        }
        override fun onError(e: Throwable) {
            Log.e("VinylDetailPres",e.message)
        }

        override fun onSubscribe(d: Disposable) {
            compositeDisposable?.add(d)
        }
    }

    override fun dispose() {
        compositeDisposable?.dispose()
    }
}