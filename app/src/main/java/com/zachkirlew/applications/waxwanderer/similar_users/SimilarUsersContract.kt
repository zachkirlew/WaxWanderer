package com.zachkirlew.applications.waxwanderer.similar_users

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


interface SimilarUsersContract {

    interface View: BaseView<Presenter> {

        fun showSimilarUsers(users: List<User>)
        fun showMatchDialog(likedUserName : String)

        fun showMessage(message : String)
    }

    interface Presenter : BasePresenter {

        fun loadSimilarUsers(userInfo : User)
        fun handleLike(likedUser : User)
    }
}