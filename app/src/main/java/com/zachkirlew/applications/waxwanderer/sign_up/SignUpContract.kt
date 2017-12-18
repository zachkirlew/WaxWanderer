package com.zachkirlew.applications.waxwanderer.sign_up

interface SignUpContract {
    interface View {

        fun startExploreActivity()

        fun showCreateUserFailedMessage()


    }

    interface Presenter {

        fun signUp(name : String, email : String, password: String)


    }
}