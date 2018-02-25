package com.zachkirlew.applications.waxwanderer.explore

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface ExploreContract {

    interface View: BaseView<Presenter> {

        fun showVinylReleases(vinyls: List<VinylRelease>)

        fun showNoVinylsView()

        fun showNoInternetMessage()

        fun startVinylPreferenceActivity()
    }

    interface Presenter : BasePresenter {

        fun addToFavourites(vinyl : VinylRelease)

        fun loadVinylPreferences()

        fun searchVinylReleases(searchText : String?)

        fun loadVinylReleases(styles: List<String>)

        fun dispose()
    }
}