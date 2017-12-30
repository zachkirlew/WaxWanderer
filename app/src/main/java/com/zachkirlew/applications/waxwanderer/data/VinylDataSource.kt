package com.zachkirlew.applications.waxwanderer.data

import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import io.reactivex.Observable

interface VinylDataSource {

    fun getVinyls(preference: VinylPreference): Observable<DiscogsResponse>
}