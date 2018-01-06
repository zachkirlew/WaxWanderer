package com.zachkirlew.applications.waxwanderer.sign_up

interface SignUpContract {
    interface View {

        fun startMatchDetailsActivity()

        fun showCreateUserFailedMessage(message : String)

        fun showNameErrorMessage(message : String)

        fun showEmailErrorMessage(message : String)

        fun showPasswordErrorMessage(message : String)
    }

    interface Presenter {

        fun validateName(name : String): Boolean

        fun validateEmail(email : String): Boolean

        fun validatePassword(password: String) : Boolean

        fun signUp(name : String, email : String,password: String)


    }
}