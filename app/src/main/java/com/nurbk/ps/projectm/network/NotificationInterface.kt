package com.nurbk.ps.projectm.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface NotificationInterface {

    @POST("send")
    fun sendRemoteMessage(
        @HeaderMap headerMap: HashMap<String, String>,
        @Body remoteSend:String
    ): Call<String>

}