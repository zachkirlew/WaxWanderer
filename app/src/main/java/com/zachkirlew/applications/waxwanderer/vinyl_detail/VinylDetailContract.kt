package com.zachkirlew.applications.waxwanderer.vinyl_detail

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Tracklist
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Video

interface VinylDetailContract {
    interface View : BaseView<Presenter> {

        fun showImageBackDrop(imageUrl: String)
        fun showDetailVinylInfo(detailVinylRelease : DetailVinylRelease)

        fun showTrackList(trackList: List<Tracklist>?)
        fun showVideos(videos : List<Video>?)

        fun showRating(starRating: Double)

        fun editButtonColor(vinylIsInFavourites : Boolean)

        fun addRemovedResult(isRemoved : Boolean)
    }

    interface Presenter {

        fun loadVinylRelease(releaseId : String)

        fun addToFavourites(vinylRelease: VinylRelease)

        fun checkInFavourites(releaseId: String)

        fun addFavouriteToRecommender(userId : String, itemId : String)

        fun removeFavouriteFromRecommender(userId : String, itemId : String)
    }
}