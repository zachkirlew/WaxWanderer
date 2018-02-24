package com.zachkirlew.applications.waxwanderer.data.remote

import io.reactivex.Single
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FCMPushService {

    @Headers( "Content-Type: application/json","Authorization: key=AAAA_UtWYlQ:APA91bFISA4t1Om_7WxXLqRwwmNzYgdXwxLcLOYFac8qmQoBEFZYO8LZ4Q2vXhc45s6zDJfI3R51kgwVgOF_fTk__2wHorSg2nPolZxHn78L8mfHz132bFZc2I8KWHWFULnu3LA55SOx")

    @POST("send")
    fun send(@Body body : PushPayload) : Single<ResponseBody>
}