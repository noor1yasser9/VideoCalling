package com.nurbk.ps.projectm.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Message
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.others.USER_DATA_LIST
import com.nurbk.ps.projectm.ui.activity.MainActivity


object NotificationUtils {
    private const val MAIN_CHANNEL_ID = "main_channel_id"
    fun createMainNotificationChannel(context: Context) {
        val soundUri =
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.notification)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.main_channel)
            val channelDescription = context.getString(R.string.main_channel_description)
            val notificationChannel = NotificationChannel(
                MAIN_CHANNEL_ID, channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = channelDescription
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationChannel.setShowBadge(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 500, 700, 900, 700, 500, 0)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            notificationChannel.setSound(soundUri, audioAttributes)
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun showBasicNotification(
        context: Context, message: com.nurbk.ps.projectm.model.Message
    ) {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(USER_DATA_LIST, message)
        val pendingIntent = TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, 0)
        val soundUri =
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.notification)
        val builder = NotificationCompat.Builder(context, MAIN_CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_message)
        builder.setContentTitle(message.name)
        builder.setContentText(message.text)
        builder.setTicker(message.text)
        builder.priority = NotificationCompat.PRIORITY_MAX
        builder.setVibrate(longArrayOf(0, 500, 700, 900, 700, 500, 0))
        builder.setSound(soundUri)

        val pBuilder1 = Person.Builder()
        pBuilder1.setName(message.name)
        pBuilder1.setKey(message.id)

        val message2 = NotificationCompat.MessagingStyle.Message(
            message.text,
            System.currentTimeMillis(),
            pBuilder1.build()
        )
        builder.setStyle(
            NotificationCompat.MessagingStyle(pBuilder1.build())
                .addMessage(message2)
        )
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(1, builder.build())
    }

}