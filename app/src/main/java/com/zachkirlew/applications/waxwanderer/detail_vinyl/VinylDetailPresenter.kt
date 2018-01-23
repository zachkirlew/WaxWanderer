package com.zachkirlew.applications.waxwanderer.detail_vinyl


import android.support.annotation.NonNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.recommendation.RecommenderImp
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class VinylDetailPresenter(private @NonNull var vinylRepository: VinylRepository,
                           private @NonNull var vinylDetailView: VinylDetailContract.View,
                           private @NonNull var recommender: RecommenderImp) : VinylDetailContract.Presenter {

    private val TAG = VinylDetailActivity::class.java.simpleName

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

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

                    }

                    override fun onSubscribe(d: Disposable) {

                    }
                })
    }

    override fun addToFavourites(vinylRelease: VinylRelease) {

        val user = mFirebaseAuth.currentUser

        val myRef = database.reference.child("favourites").child(user?.uid).child(vinylRelease.id.toString())

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //if user has already favourited track then remove
                if (dataSnapshot.exists()) {
                    myRef.setValue(null)
                    vinylDetailView.showMessage("Removed from favourites")
                    vinylDetailView.editButtonColor(false)
                    removeFavouriteFromRecommender(user?.uid!!,vinylRelease.id.toString())
                } else {
                    myRef.setValue(vinylRelease)
                    vinylDetailView.showMessage("Successfully added to favourites")
                    vinylDetailView.editButtonColor(true)
                    addFavouriteToRecommender(user?.uid!!,vinylRelease.id.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
    }

    override fun checkInFavourites(releaseId: String) {
        val user = mFirebaseAuth.currentUser

        val myRef = database.reference.child("favourites").child(user?.uid).child(releaseId)

        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //if user has already favourited track
                if (dataSnapshot.exists()) {
                    vinylDetailView.editButtonColor(true)
                } else {
                    vinylDetailView.editButtonColor(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException())
            }
        })
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

    object recommenderObserver : SingleObserver<String>{
        override fun onSuccess(responseString: String) {
            Log.i("VinylDetailPres",responseString)
        }
        override fun onError(e: Throwable) {
            Log.e("VinylDetailPres",e.message)
        }

        override fun onSubscribe(d: Disposable) {
        }
    }
}