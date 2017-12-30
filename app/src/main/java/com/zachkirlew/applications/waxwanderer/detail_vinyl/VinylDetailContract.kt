package com.zachkirlew.applications.waxwanderer.detail_vinyl

import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease

interface VinylDetailContract {
    interface View {

        fun showImageBackDrop(imageUrl: String)

    }

    interface Presenter {


        fun loadVinylRelease(releaseId : String)

    }
}