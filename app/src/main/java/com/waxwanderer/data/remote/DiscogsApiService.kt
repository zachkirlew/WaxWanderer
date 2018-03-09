package com.waxwanderer.data.remote

import com.waxwanderer.data.model.discogs.DiscogsResponse
import com.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

interface DiscogsApiService {

    @Headers("Authorization: Discogs key=ixsPyvNUQWBaufFSeKqJ, secret=OrEgnXaJwlSUrMmxytmfFkVRbjzAPitg")

    @GET("/database/search")
    fun searchReleases(@QueryMap parameters: Map<String, String>) : Observable<DiscogsResponse>

    @Headers("Authorization: Discogs key=ixsPyvNUQWBaufFSeKqJ, secret=OrEgnXaJwlSUrMmxytmfFkVRbjzAPitg")

    @GET("/releases/{release_id}")
    fun release(@Path("release_id") releaseId : String) : Single<DetailVinylRelease>
}