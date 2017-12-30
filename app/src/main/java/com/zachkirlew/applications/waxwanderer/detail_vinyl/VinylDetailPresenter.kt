package com.zachkirlew.applications.waxwanderer.detail_vinyl


import android.support.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class VinylDetailPresenter(private @NonNull var vinylRepository: VinylRepository, private @NonNull var vinylDetailView: VinylDetailContract.View) : VinylDetailContract.Presenter {


    private val TAG = VinylDetailActivity::class.java.simpleName

    private val mFirebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun loadVinylRelease(releaseId: String) {

        vinylRepository.getVinyl(releaseId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<DetailVinylRelease> {
                    override fun onSuccess(detailVinylRelease: DetailVinylRelease) {
                        println(detailVinylRelease.images?.get(0)?.uri)
                        println(detailVinylRelease.images?.size)

                        vinylDetailView.showImageBackDrop(detailVinylRelease.images?.get(0)?.uri!!)
                    }
                    override fun onError(e: Throwable) {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }
                })
    }
}