package com.waxwanderer.friends.search

import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.User

interface FriendsSearchContract {

    interface View: BaseView<Presenter> {

        fun showUser(user: User?)

        fun showNoUsersView(show : Boolean)

        fun handleSearch(queryText : String?)

        fun setIsSearching(isSearching : Boolean)

    }

    interface Presenter{

        fun loadUsers(nameQuery : String?)

        fun sendFriendRequest(user : User)

        fun dispose()

    }
}