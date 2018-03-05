package com.waxwanderer.vinyl_preferences

import com.waxwanderer.data.model.Style

interface VinylPreferencesContract {
    interface View {

        fun startNextActivity()

        fun showGenres(genres : List<String>)

        fun showStyles(styles : List<Style>)

        fun showUsersPreferredStyles(styles : List<Style>)

    }

    interface Presenter {

        fun savePreferences(selectedStyles : List<Style>)

        fun loadGenres()

        fun loadStyles(genre : String)

        fun loadVinylPrefs()

        fun dispose()

    }
}