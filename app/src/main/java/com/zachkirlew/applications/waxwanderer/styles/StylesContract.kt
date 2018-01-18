package com.zachkirlew.applications.waxwanderer.styles

import com.zachkirlew.applications.waxwanderer.data.model.Style

interface StylesContract {
    interface View {

        fun startNextActivity()

        fun showGenres(genres : List<String>)

        fun showStyles(styles : List<Style>)

    }

    interface Presenter {

        fun savePreferences(selectedGenre : String, selectedStyles : List<String>)

        fun loadGenres()

        fun loadStyles(genre : String)

    }
}