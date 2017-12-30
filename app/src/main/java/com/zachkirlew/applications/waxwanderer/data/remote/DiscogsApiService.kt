package com.zachkirlew.applications.waxwanderer.data.remote

import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import io.reactivex.Observable
import retrofit2.http.*

interface DiscogsApiService {

    @Headers("Authorization: Discogs key=ixsPyvNUQWBaufFSeKqJ, secret=OrEgnXaJwlSUrMmxytmfFkVRbjzAPitg")

    @GET("/database/search")
    fun searchReleases(@QueryMap parameters: Map<String, String>) : Observable<DiscogsResponse>

    @GET("/releases/{release_id}/rating")
    fun releaseRating(@Path("release_id") releaseId : String) : Observable<DiscogsResponse>
}