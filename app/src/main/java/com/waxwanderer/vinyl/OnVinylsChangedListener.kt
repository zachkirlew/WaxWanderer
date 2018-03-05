package com.waxwanderer.vinyl

import com.waxwanderer.data.model.discogs.VinylRelease

interface OnVinylsChangedListener {
    fun onFiltered(isEmpty : Boolean)
    fun onAddedToFavourites(vinyl : VinylRelease)
    fun onRemovedFromFavourites(vinylId : Int)
}