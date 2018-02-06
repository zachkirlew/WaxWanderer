package com.zachkirlew.applications.waxwanderer.data.model

import com.zachkirlew.applications.waxwanderer.data.model.discogs.VinylRelease


data class UserCard(val user : User, val vinylPreference : List<String>, val favourites : List<VinylRelease>? )