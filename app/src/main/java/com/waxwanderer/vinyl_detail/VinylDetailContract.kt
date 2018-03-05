package com.waxwanderer.vinyl_detail

import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.discogs.VinylRelease
import com.waxwanderer.data.model.discogs.detail.DetailVinylRelease

interface VinylDetailContract {
    interface View : BaseView<Presenter> {

        fun showInfo(detailVinylRelease: DetailVinylRelease)
        fun showRating(starRating: Double)

        fun editButtonColor(vinylIsInFavourites : Boolean)
    }

    interface Presenter{

        fun loadVinylRelease(releaseId : String)

        fun addToFavourites(vinylRelease: VinylRelease)

        fun checkInFavourites(releaseId: String)

        fun addFavouriteToRecommender(userId : String, itemId : String)

        fun removeFavouriteFromRecommender(userId : String, itemId : String)

        fun dispose()
    }
}