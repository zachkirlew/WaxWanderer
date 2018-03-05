package com.waxwanderer.recommend

import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.User
import com.waxwanderer.data.model.discogs.VinylRelease

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