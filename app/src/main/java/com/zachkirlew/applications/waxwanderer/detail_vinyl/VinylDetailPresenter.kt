package com.zachkirlew.applications.waxwanderer.detail_vinyl


import android.support.annotation.NonNull
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class VinylDetailPresenter(private @NonNull var vinylRepository: VinylRepository, private @NonNull var vinylDetailView: VinylDetailContract.View) : VinylDetailContract.Presenter {


    private val TAG = VinylDetailActivity::class.java.simpleName

    override fun loadVinylRelease(releaseId: String) {

        vinylRepository.getVinyl(releaseId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<DetailVinylRelease> {
                    override fun onSuccess(detailVinylRelease: DetailVinylRelease) {

                        detailVinylRelease.images?.get(0)?.uri?.let { vinylDetailView.showImageBackDrop(detailVinylRelease.images?.get(0)?.uri!!) }

                        vinylDetailView.showDetailVinylInfo(detailVinylRelease)

                        detailVinylRelease.tracklist?.let {vinylDetailView.showTrackList(detailVinylRelease.tracklist) }

                        detailVinylRelease.videos?.let {vinylDetailView.showVideos(detailVinylRelease.videos)}

                        vinylDetailView.showRating(detailVinylRelease.community?.rating?.average!!)
                    }
                    override fun onError(e: Throwable) {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }
                })
    }
}