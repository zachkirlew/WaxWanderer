package com.zachkirlew.applications.waxwanderer.detail_vinyl

import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Tracklist
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.Video

interface VinylDetailContract {
    interface View {

        fun showImageBackDrop(imageUrl: String)
        fun showDetailVinylInfo(detailVinylRelease : DetailVinylRelease)

        fun showTrackList(trackList: List<Tracklist>?)
        fun showVideos(videos : List<Video>?)

        fun showRating(starRating: Double)

        fun showMessage(message : String)
    }

    interface Presenter {

        fun loadVinylRelease(releaseId : String)

        fun addToFavourites(vinylRelease: VinylRelease)
    }
}