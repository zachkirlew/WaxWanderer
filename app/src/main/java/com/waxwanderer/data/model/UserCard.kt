package com.waxwanderer.data.model

import com.waxwanderer.data.model.discogs.VinylRelease


data class UserCard(val user : User, val vinylPreference : List<String>, val favourites : List<VinylRelease>? )