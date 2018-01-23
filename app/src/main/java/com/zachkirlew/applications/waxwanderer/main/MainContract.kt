package com.zachkirlew.applications.waxwanderer.main


interface MainContract {
    interface View {

        fun showProfilePicture(imageUrl : String)
        fun showDisplayName(displayName : String)
        fun startLoginActivity()

        fun startExploreFragment()
    }

    interface Presenter {

        fun setAuthListener()

        fun removeAuthListener()

        fun loadUserDetails()
    }
}