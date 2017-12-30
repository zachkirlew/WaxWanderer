package com.zachkirlew.applications.waxwanderer.data

import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import com.zachkirlew.applications.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import io.reactivex.Observable
import io.reactivex.Single

interface VinylDataSource {

    fun getVinyls(preference: VinylPreference): Observable<DiscogsResponse>

    fun getVinyl(releaseId: String): Single<DetailVinylRelease>
}