package com.waxwanderer.notifications

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
import com.waxwanderer.R
import com.waxwanderer.main.MainActivity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import com.squareup.picasso.Target
import android.os.Looper
import android.support.v4.app.TaskStackBuilder
import com.waxwanderer.message.MessageActivity


class FirebaseMessagingService : FirebaseMessagingService() {

    private val TAG: String = FirebaseMessagingService::class.java.simpleName

    private lateinit var notificationBuilder: NotificationCompat.Builder

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {


        // Check if message contains a data payload.
        if (remoteMessage?.data != null) {

            println("Message received")

            val data = remoteMessage.data

            sendNotification(data["title"], data["message"], data)
        }
    }

    private fun sendNotification(title: String?, messageBody: String?, data: Map<String, String>) {

        val notificationType = data["type"]

        var resultIntent : Intent? = null

        when(notificationType){
            "message"->{

                // Create an Intent for the activity you want to start
                resultIntent = Intent(this, MessageActivity::class.java)
                resultIntent.putExtra("matchedUserId",data["from_id"])
            }
            "friend_requested"->{
                // Create an Intent for the activity you want to start
                resultIntent = Intent(this, MainActivity::class.java)
                resultIntent.putExtra("tabToShow","friends")
            }

            "friend_added"->{
                // Create an Intent for the activity you want to start
                resultIntent = Intent(this, MainActivity::class.java)
                resultIntent.putExtra("tabToShow","friends")
            }
        }

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent!!)

        // Get the PendingIntent containing the entire back stack
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)!!

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


        if (data["release_image"] != null) {
            val uiHandler = Handler(Looper.getMainLooper())
            uiHandler.post({
                getImage(data["release_image"]!!)
            })
        } else {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
    }

    private fun getImage(url: String) {
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

