package com.nurbk.ps.projectm.service

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nurbk.ps.projectm.model.CallingData
import com.nurbk.ps.projectm.model.PushCalling
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.ui.activity.MainActivity
import java.lang.reflect.Type


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

        val type = remoteMessage.data[REMOTE_MSG_TYPE]

        Log.e("ttttttt", "asdfasdfsadf")
        if (!type.isNullOrEmpty()) {
            val dataJson =
                Gson().toJson(remoteMessage.data)
            val dataCalling = Gson().fromJson(dataJson.toString(), CallingData::class.java)
            val intents = Intent(REMOTE_MSG_INVITATION_RESPONSE)
            intents.putExtra("data", dataCalling)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intents)
        }

    }


}