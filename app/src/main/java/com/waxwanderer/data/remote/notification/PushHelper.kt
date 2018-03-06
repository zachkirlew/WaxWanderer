package com.waxwanderer.data.remote.notification

import com.waxwanderer.util.ConfigHelper
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import android.app.Activity
import com.waxwanderer.data.model.discogs.VinylRelease
import com.waxwanderer.data.model.notifications.PushPayload
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

    fun sendNotification(title : String,
                         message : String,
                         token : String,
                         type : String,
                         from : String,
                         attachedRelease : VinylRelease?): Single<ResponseBody> {

        val pushPayload = PushPayload()
        pushPayload.to = token

        val dataMap = HashMap<String, Any>()

        dataMap["title"] = title
        dataMap["message"] = message
        dataMap["type"] = type
        dataMap["from_id"] = from

        if(attachedRelease!=null){

            if(attachedRelease.thumb!=null)
                dataMap["release_image"] = attachedRelease.thumb!!
            dataMap["message"] = "Check out this record!"
            dataMap["release_title"] = attachedRelease.title!!
            dataMap["release_no"] = attachedRelease.catno!!
        }

        pushPayload.data = dataMap

        return fcmPushService.send(key, pushPayload)
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