package com.zachkirlew.applications.waxwanderer.data.remote

import com.zachkirlew.applications.waxwanderer.data.VinylDataSource
import com.zachkirlew.applications.waxwanderer.data.model.DiscogsResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class VinylsRemoteSource private constructor() : VinylDataSource{

    private val discogsService : DiscogsApiService

    init {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.discogs.com")
                .build()

        discogsService = retrofit.create<DiscogsApiService>(DiscogsApiService::class.java)
    }

    override fun getVinyls(): Observable<DiscogsResponse> {

        val parameters : HashMap<String, String> = HashMap()

        parameters.put("q","Pearson Sound")
        parameters.put("per_page","50")
        parameters.put("format","vinyl")
        parameters.put("type","release")

        return discogsService.searchReleases(parameters)
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