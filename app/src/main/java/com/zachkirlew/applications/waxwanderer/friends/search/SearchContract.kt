package com.zachkirlew.applications.waxwanderer.friends.search

import com.zachkirlew.applications.waxwanderer.base.BaseView
import com.zachkirlew.applications.waxwanderer.data.model.User

interface SearchContract {

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