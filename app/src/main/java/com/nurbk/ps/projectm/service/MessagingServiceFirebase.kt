package com.nurbk.ps.projectm.service

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.model.modelNetwork.CallingData
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.utils.NotificationUtils


class MessagingServiceFirebase : FirebaseMessagingService() {
    private var broadcaster: LocalBroadcastManager? = null


    override fun onCreate() {
        super.onCreate()
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("FCM", "Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val type = remoteMessage.data

        if (!type.isNullOrEmpty()) {
            if (type["meetingRoom"] != null) {
                val dataJson =
                    Gson().toJson(type)
                val dataCalling = Gson().fromJson(dataJson.toString(), CallingData::class.java)
                val intents = Intent(REMOTE_MSG_INVITATION_RESPONSE)
                intents.putExtra("data", dataCalling)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intents)
            } else {
                val dataJson =
                    Gson().toJson(type)
                val message = Gson().fromJson(dataJson.toString(), Message::class.java)

                NotificationUtils.showBasicNotification(this, message)
            }
        }

    }


}