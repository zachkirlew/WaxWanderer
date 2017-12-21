package com.zachkirlew.applications.waxwanderer.sign_up

interface SignUpContract {
    interface View {

        fun startStylesActivity()

        fun showCreateUserFailedMessage(message : String)

        fun showNameErrorMessage(message : String)

        fun showEmailErrorMessage(message : String)

        fun showDOBErrorMessage(message: String)

        fun showPasswordErrorMessage(message : String)

        fun showDateFormatted(date : String)
    }

    interface Presenter {

        fun getFormattedDate(year : Int, month : Int, day : Int)

        fun validateName(name : String): Boolean

        fun validateEmail(email : String): Boolean

        fun validatePassword(password: String) : Boolean

        fun signUp(name : String, email : String,password: String)


    }
}