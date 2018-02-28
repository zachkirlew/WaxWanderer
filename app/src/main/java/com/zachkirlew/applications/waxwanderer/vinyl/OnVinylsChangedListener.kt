package com.zachkirlew.applications.waxwanderer.vinyl

import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease

interface OnVinylsChangedListener {
    fun onFiltered(isEmpty : Boolean)
    fun onAddedToFavourites(vinyl : VinylRelease)
    fun onRemovedFromFavourites(vinylId : Int)
}