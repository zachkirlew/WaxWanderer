package com.zachkirlew.applications.waxwanderer.data.remote

import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PushHelper private constructor(){

    private val fcmPushService : FCMPushService

    init {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .build()

        fcmPushService = retrofit.create<FCMPushService>(FCMPushService::class.java)
    }

    fun sendNotification(payload: PushPayload): Single<ResponseBody> {
        return fcmPushService.send(payload)
    }

    companion object {

        private var INSTANCE: PushHelper? = null

        val instance: PushHelper
            get() {
                if (INSTANCE == null) {
                    INSTANCE = PushHelper()
                }
                return INSTANCE as PushHelper
            }
    }
}