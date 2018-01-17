package com.zachkirlew.applications.waxwanderer.data

import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import io.reactivex.Observable
import io.reactivex.Single

interface VinylDataSource {

    fun getVinyls(style : String): Observable<DiscogsResponse>

    fun getVinyl(releaseId: String): Single<DetailVinylRelease>

    fun searchVinyl(searchText: String): Observable<DiscogsResponse>
}