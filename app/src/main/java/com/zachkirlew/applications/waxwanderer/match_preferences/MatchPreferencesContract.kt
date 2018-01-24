package com.zachkirlew.applications.waxwanderer.match_preferences

interface MatchPreferencesContract {
    interface View {

        fun startStylesActivity()

        fun showCreateUserFailedMessage(message : String)

        fun showDOBErrorMessage(message: String)

        fun showDateFormatted(date : String)

        fun showLocationErrorMessage(message: String)

    }

    interface Presenter {

        fun getFormattedDate(year : Int, month : Int, day : Int)

        fun submitDetails(userGender : String,userLocation : String?,matchGender : String,minMatchAge : Int,maxMatchAge : Int)

    }
}