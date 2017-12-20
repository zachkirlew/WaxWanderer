package com.zachkirlew.applications.waxwanderer.sign_up

import java.util.*

interface SignUpContract {
    interface View {

        fun startStylesActivity()

        fun showCreateUserFailedMessage()


    }

    interface Presenter {

        fun signUp(name : String, email : String, dob : Date, password: String)


    }
}