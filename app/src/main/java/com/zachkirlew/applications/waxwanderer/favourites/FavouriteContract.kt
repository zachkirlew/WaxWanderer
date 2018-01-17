package com.zachkirlew.applications.waxwanderer.favourites

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface FavouriteContract {

    interface View: BaseView<Presenter> {

        fun showFavouriteVinyls(vinyls: List<VinylRelease>)
        fun showVinylReleaseDetailsUI()

        fun showNoVinylsView()
    }

    interface Presenter {
        fun loadFavouriteVinyls()
        fun loadFavouriteVinyls(userId : String)
    }
}