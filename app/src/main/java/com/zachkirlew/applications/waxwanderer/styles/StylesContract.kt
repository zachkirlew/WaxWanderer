package com.zachkirlew.applications.waxwanderer.styles

interface StylesContract {
    interface View {

        fun startExploreActivity()

        fun showGenres(genres : List<String>)

    }

    interface Presenter {

        fun loadGenres()

    }
}