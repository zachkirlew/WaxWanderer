package com.zachkirlew.applications.waxwanderer.data

import android.support.annotation.NonNull
import com.zachkirlew.applications.waxwanderer.data.model.VinylPreference
import com.zachkirlew.applications.waxwanderer.data.model.discogs.DiscogsResponse
import io.reactivex.Observable

class VinylRepository private constructor(private @NonNull val vinylRemoteDataSource: VinylDataSource): VinylDataSource{



    override fun getVinyls(preference: VinylPreference): Observable<DiscogsResponse> {
        return vinylRemoteDataSource.getVinyls(preference)
    }

    companion object {

        private var INSTANCE: VinylRepository? = null

        fun getInstance(vinylRemoteDataSource: VinylDataSource): VinylRepository{
            if (INSTANCE == null) {
                INSTANCE = VinylRepository(vinylRemoteDataSource)
            }
            return INSTANCE as VinylRepository

        }
    }
}