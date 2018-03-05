package com.waxwanderer.data

import com.waxwanderer.data.model.discogs.DiscogsResponse
import com.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import io.reactivex.Observable
import io.reactivex.Single

interface VinylDataSource {

    fun getVinyls(params: HashMap<String, String>, pageNumber: Int): Observable<DiscogsResponse>

    fun getVinyl(releaseId: String): Single<DetailVinylRelease>
}