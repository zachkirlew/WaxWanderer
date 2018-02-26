package com.zachkirlew.applications.waxwanderer.explore

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease


interface ExploreContract {

    interface View: BaseView<Presenter> {

        fun showVinylReleases(vinyls: List<VinylRelease>)

        fun showQuickViewDialog(detailedVinylRelease: DetailVinylRelease)

        fun showNoVinylsView()

        fun showNoInternetMessage()

        fun startVinylDetailActivity(vinyl: VinylRelease)

        fun startVinylPreferenceActivity()
    }

    interface Presenter : BasePresenter {

        fun addToFavourites(vinyl : VinylRelease)

        fun loadVinylPreferences()

        fun loadVinylRelease(releaseId : String)

        fun searchVinylReleases(searchText : String?)

        fun loadVinylReleases(styles: List<String>)

        fun dispose()
    }
}