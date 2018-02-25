package com.zachkirlew.applications.waxwanderer.data.remote.notification

import com.zachkirlew.applications.waxwanderer.util.ConfigHelper
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import android.app.Activity
import com.zachkirlew.applications.waxwanderer.data.remote.PushPayload
import java.lang.ref.WeakReference


class PushHelper private constructor(activity: Activity){

    private val fcmPushService : FCMPushService
    private val key : String

    private val context: WeakReference<Activity>

    init {
        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://fcm.googleapis.com/fcm/")
                .build()

        context = WeakReference(activity)

        key = ConfigHelper.getConfigValue(context.get()!!, "sender_id")!!

        fcmPushService = retrofit.create<FCMPushService>(FCMPushService::class.java)
    }

    fun sendNotification(payload: PushPayload): Single<ResponseBody> {
        println(key)
        return fcmPushService.send(key, payload)
    }

    companion object {

        private var INSTANCE: PushHelper? = null

        fun getInstance(activity: Activity): PushHelper {
            if (INSTANCE == null) {
                INSTANCE = PushHelper(activity)
            }
            return INSTANCE as PushHelper
        }
    }
}