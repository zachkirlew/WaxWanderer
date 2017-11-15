package com.zachkirlew.applications.waxwanderer.data.remote

import com.zachkirlew.applications.waxwanderer.data.model.DiscogsResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface DiscogsApiService {

    //auth
    @Headers("Authorization: Discogs key=ixsPyvNUQWBaufFSeKqJ, secret=OrEgnXaJwlSUrMmxytmfFkVRbjzAPitg")

    @GET("search")
    fun searchReleases(@Query("q") query : String,
                       @Query("per_page") perPage : Int,
                       @Query("format") format : String) : Observable<DiscogsResponse>

}