package com.zachkirlew.applications.waxwanderer.dob

interface DOBContract {
    interface View {

        fun startStylesActivity()

        fun showCreateUserFailedMessage(message : String)

        fun showDOBErrorMessage(message: String)

    }

    interface Presenter {

        fun submitDOB(year : Int, month : Int, day : Int)

    }
}