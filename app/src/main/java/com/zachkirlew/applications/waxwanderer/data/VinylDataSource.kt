package com.zachkirlew.applications.waxwanderer.data

import com.zachkirlew.applications.waxwanderer.data.model.DiscogsResponse
import io.reactivex.Observable

interface VinylDataSource {

    fun getVinyls(): Observable<DiscogsResponse>
}