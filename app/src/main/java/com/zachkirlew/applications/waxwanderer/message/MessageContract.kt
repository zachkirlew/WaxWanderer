package com.zachkirlew.applications.waxwanderer.message

import com.zachkirlew.applications.waxwanderer.base.BasePresenter
import com.zachkirlew.applications.waxwanderer.data.model.Message

interface MessageContract {

    interface View {

        fun showMessage(message : Message)

    }

    interface Presenter : BasePresenter {

        fun loadMessages()
        fun sendMessage(message: Message)

    }
}