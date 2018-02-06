package com.zachkirlew.applications.waxwanderer.explore

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface ExploreContract {

    interface View: BaseView<Presenter> {

        fun showVinylReleases(vinyls: List<VinylRelease>)

        fun showNoVinylsView()

        fun showNoInternetMessage()
    }

    interface Presenter : BasePresenter {

        fun loadVinylReleases()

        fun searchVinylReleases(searchText : String?)

        fun dispose()
    }
}