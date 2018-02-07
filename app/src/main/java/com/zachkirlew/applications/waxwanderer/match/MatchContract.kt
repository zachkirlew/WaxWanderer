package com.zachkirlew.applications.waxwanderer.match

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.UserCard
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface MatchContract {

    interface View: BaseView<Presenter> {

        fun startRecommendationsActivity()

        fun addUserCard(userCard : UserCard)

        fun showMatchDialog(likedUserName : String)

        override fun showMessage(message : String?)
    }

    interface Presenter : BasePresenter {

        fun likeUser(likedUser : User)

        fun dispose()
    }
}