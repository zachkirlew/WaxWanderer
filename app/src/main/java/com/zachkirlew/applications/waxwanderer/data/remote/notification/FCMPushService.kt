package com.zachkirlew.applications.waxwanderer.data.remote.notification

import com.zachkirlew.applications.waxwanderer.data.remote.PushPayload
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMPushService {

    @Headers( "Content-Type: application/json")

    @POST("send")
    fun send(@Header("Authorization") key : String, @Body body : PushPayload) : Single<ResponseBody>
}