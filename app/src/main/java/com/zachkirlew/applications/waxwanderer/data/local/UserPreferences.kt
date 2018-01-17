package com.zachkirlew.applications.waxwanderer.data.local

class UserPreferences : Preferences() {
    var minMatchAge by intPref()
    var maxMatchAge by intPref()
    var matchGender by stringPref()
}