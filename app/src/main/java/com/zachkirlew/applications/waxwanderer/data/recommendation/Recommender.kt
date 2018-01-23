package com.zachkirlew.applications.waxwanderer.data.recommendation

import io.reactivex.Observable
import io.reactivex.Single

interface Recommender {

    fun addFavourite(userId : String, itemId : String): Single<String>
    fun removeFavourite(userId : String, itemId : String): Single<String>
    fun addRating(userId : String, itemId : String,rating : Double): Single<String>
    fun recommendUserToUser(userId: String, count : Long) : Observable<List<String>>
}