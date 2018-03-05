package com.waxwanderer.main

import android.support.v4.app.Fragment


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

        fun signOut()

        fun removeDisposables(fragment : Fragment?)

        fun dispose()
    }
}