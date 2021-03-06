package com.waxwanderer.data.remote

import com.waxwanderer.data.VinylDataSource
import com.waxwanderer.data.model.discogs.DiscogsResponse
import com.waxwanderer.data.model.discogs.detail.DetailVinylRelease
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class VinylsRemoteSource private constructor() : VinylDataSource {


    private val discogsService : DiscogsApiService

    init {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.discogs.com")
                .build()

        discogsService = retrofit.create<DiscogsApiService>(DiscogsApiService::class.java)
    }

    override fun getVinyl(releaseId: String): Single<DetailVinylRelease> {
        return discogsService.release(releaseId)
    }

    override fun getVinyls(params: HashMap<String, String>, pageNumber: Int): Observable<DiscogsResponse> {

        params["per_page"] = "50"
        params["page"] = pageNumber.toString()
        params["format"] = "vinyl"
        params["type"] = "release"

        return discogsService.searchReleases(params)
    }

    companion object {

        private var INSTANCE: VinylsRemoteSource? = null

        val instance: VinylsRemoteSource
            get() {
                if (INSTANCE == null) {
                    INSTANCE = VinylsRemoteSource()
                }
                return INSTANCE as VinylsRemoteSource
            }
    }
}