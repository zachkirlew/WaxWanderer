package com.zachkirlew.applications.waxwanderer.recommend

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User
import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease

interface RecommendVinylDialogContract {

    interface View: BaseView<Presenter> {

        fun showFriend(user: User?)

        fun showNoFriendsView(show : Boolean)

        fun dismiss()
    }

    interface Presenter{

        fun loadFriends()

        fun sendRecommendation(user : User, vinyl : VinylRelease)

        fun dispose()

    }
}