package com.zachkirlew.applications.waxwanderer.data.model

import java.util.*

data class User(val name: String, val email : String, val dob: Date?, val gender : String?,val Location : String?,val matchPreference: MatchPreference?,val vinylPreference: VinylPreference?)