package com.zachkirlew.applications.waxwanderer.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import com.zachkirlew.applications.waxwanderer.R
import com.zachkirlew.applications.waxwanderer.main.MainActivity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import com.squareup.picasso.Target
import android.os.Looper




class FirebaseMessagingService : FirebaseMessagingService() {

    private val TAG: String = FirebaseMessagingService::class.java.simpleName

    private lateinit var notificationBuilder : NotificationCompat.Builder

    private val notificationManager by lazy{getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager}

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {

        // Check if message contains a notification payload.
        if (remoteMessage?.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification?.body)

            sendNotification(remoteMessage.notification?.title, remoteMessage.notification?.body, remoteMessage.data)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_stat_thumbnail_icons8_music_record)
                .setColor(resources.getColor(R.color.colorAccent))
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Wax Wanderer",
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        if(data.isEmpty()){
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
        else{
            if (data["release_image"] != null){
                val uiHandler = Handler(Looper.getMainLooper())
                uiHandler.post({
                    getImage(data["release_image"]!!)
                })
            }
        }
    }

    private fun getImage(url : String){
        Picasso.with(this)
                .load(url)
                .into(notificationImageCallback)
    }

    private val notificationImageCallback = object : Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapFailed(errorDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            notificationBuilder.setLargeIcon(bitmap)

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
    }

}

