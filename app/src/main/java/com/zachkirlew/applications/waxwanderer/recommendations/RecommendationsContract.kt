package com.zachkirlew.applications.waxwanderer.recommendations

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.data.model.User


interface RecommendationsContract {

    interface View {

        fun showMessage(message : String?)

        fun showNoRecommendationsView()

        fun showRecommendedUser(user : User)

        fun removeUser(position : Int)

    }

    interface Presenter : BasePresenter {

        fun loadLikes()

        fun loadRecommendedUsers()

        fun likeUser(userId : String,position : Int)
    }
}