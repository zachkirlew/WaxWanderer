package com.waxwanderer.favourites

import com.waxwanderer.base.BasePresenter
import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.discogs.VinylRelease
import com.waxwanderer.data.model.discogs.detail.DetailVinylRelease


interface FavouriteContract {

    interface View: BaseView<Presenter> {

        fun showFavouriteVinyls(vinyls: List<VinylRelease>)

        fun showVinylRemoved(vinylId : Int)

        fun showQuickViewDialog(detailVinylRelease: DetailVinylRelease)

        fun startVinylDetailActivity(vinyl: VinylRelease)
    }

    interface Presenter : BasePresenter {

        fun loadFavouriteVinyls()

        fun loadFavouriteVinyls(userId : String)

        fun loadVinylRelease(releaseId : String)

        fun removeVinylFromFavourites(vinylId : Int)

        fun dispose()
    }
}