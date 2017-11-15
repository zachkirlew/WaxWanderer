package com.zachkirlew.applications.waxwanderer.explore

import android.support.annotation.NonNull
import com.zachkirlew.applications.waxwanderer.data.VinylRepository
import com.zachkirlew.applications.waxwanderer.data.model.DiscogsResponse
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ExplorePresenter(private @NonNull var vinylRepository: VinylRepository, private @NonNull var exploreView: ExploreContract.View) : ExploreContract.Presenter  {

    init{
        exploreView.setPresenter(this)
    }

    override fun start() {
        loadVinylReleases()
    }

    override fun loadVinylReleases() {

        vinylRepository.getVinyls()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<DiscogsResponse>{
                    override fun onNext(response: DiscogsResponse) {

                        val results = response.results


                        results?.let {exploreView.showVinylReleases(results)  }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                })



    }

    override fun openTaskDetails() {

    }


}