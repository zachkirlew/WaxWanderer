package com.zachkirlew.applications.waxwanderer.user_detail

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


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