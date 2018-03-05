package com.waxwanderer.user_detail

import com.waxwanderer.base.BasePresenter
import com.waxwanderer.data.model.discogs.VinylRelease


interface UserDetailContract {

    interface View {

        fun showUserStyles(stylesText : String)

        fun showMessage(message : String)
        fun showUserFavourites(favourites : List<VinylRelease>)

        fun showNoFavouritesView()
    }

    interface Presenter : BasePresenter {

        fun loadUserFavourites(userId : String)

        fun loadUserStyles(userId : String)

        fun dispose()
    }
}