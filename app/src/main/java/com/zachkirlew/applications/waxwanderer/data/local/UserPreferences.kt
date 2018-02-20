package com.zachkirlew.applications.waxwanderer.data.local

class UserPreferences : Preferences() {
    var minMatchAge by intPref(null,18)
    var maxMatchAge by intPref(null,100)
    var matchGender by stringPref(null,"Everyone!")
}