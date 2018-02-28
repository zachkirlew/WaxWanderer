package com.zachkirlew.applications.waxwanderer.vinyl

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease


interface VinylContract {

    interface View: BaseView<Presenter> {

        fun showVinylReleases(vinyls: List<VinylRelease>)

        fun showQuickViewDialog(detailedVinylRelease: DetailVinylRelease)

        fun showNoVinylsView()

        fun showNoInternetMessage()

        fun startVinylDetailActivity(vinyl: VinylRelease)

        fun startVinylPreferenceActivity()

        fun setRefreshing(isRefreshing : Boolean)

        fun clearVinyls()

        fun setEndOfList(isEnd : Boolean)

    }

    interface Presenter : BasePresenter {

        fun addToFavourites(vinyl : VinylRelease)

        fun loadVinylRelease(releaseId : String)

        fun loadVinylReleases(queryParams: HashMap<String, String>, pageNumber: Int = 0)

        fun dispose()

        fun onLoadNextPage()

        fun refresh()
    }
}