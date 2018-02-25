package com.zachkirlew.applications.waxwanderer.explore

import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease

interface OnAddToFavouritesListener {

    fun onAddedToFavourites(vinyl : VinylRelease)
}

