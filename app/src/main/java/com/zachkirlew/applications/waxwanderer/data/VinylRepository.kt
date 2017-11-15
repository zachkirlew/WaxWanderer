package com.zachkirlew.applications.waxwanderer.data

import android.support.annotation.NonNull
import com.zachkirlew.applications.waxwanderer.data.model.DiscogsResponse
import io.reactivex.Observable

class VinylRepository private constructor(private @NonNull val vinylRemoteDataSource: VinylDataSource): VinylDataSource{

    override fun getVinyls(): Observable<DiscogsResponse> {
        return vinylRemoteDataSource.getVinyls()
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