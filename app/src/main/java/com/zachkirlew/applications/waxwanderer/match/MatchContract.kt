package com.zachkirlew.applications.waxwanderer.match

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface MatchContract {

    interface View: BaseView<Presenter> {

        fun startRecommendationsActivity()

        fun showUsers(users: List<User>)
        fun showMatchDialog(likedUserName : String)

        override fun showMessage(message : String?)

        fun showUserFavourites(vinyls : List<VinylRelease>, viewPosition: Int)

        fun showNoUserFavourites()

        fun showVinylPreference(commaSeparatedStyles : String, viewPosition: Int)
    }

    interface Presenter : BasePresenter {

        fun handleLike(likedUser : User)
        fun loadUserFavourites(userId : String?, viewPosition : Int)
        fun loadVinylPreference(userId: String?,viewPosition: Int)

        fun dispose()
    }
}