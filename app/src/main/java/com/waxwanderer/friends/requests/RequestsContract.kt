package com.waxwanderer.friends.requests

import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.User

interface RequestsContract {

    interface View: BaseView<Presenter> {

        fun showRequest(user: User?)

        fun removeRequestFromList(userId : String)

        fun showNoRequestsView(show : Boolean)

        fun showFriendDialog(userName : String)
    }

    interface Presenter{

        fun loadRequests()

        fun acceptRequest(befriendedUser : User)

        fun deleteRequest(matchId : String)

        fun dispose()

    }
}