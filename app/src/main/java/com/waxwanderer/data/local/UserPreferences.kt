package com.waxwanderer.data.local

import com.waxwanderer.data.local.Preferences

class UserPreferences : Preferences() {
    var pushToken by stringPref()
    var minMatchAge by intPref(null,18)
    var maxMatchAge by intPref(null,100)
    var matchGender by stringPref(null,"Everyone!")
}