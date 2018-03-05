package com.waxwanderer.leaderboard

import com.waxwanderer.base.BaseView
import com.waxwanderer.data.model.User


interface LeaderBoardContract {

    interface View: BaseView<Presenter> {

        fun showUsers(users: List<User>)
    }

    interface Presenter{

        fun loadUsers()

        fun dispose()
    }
}