package com.nurbk.ps.projectm.network

import com.nurbk.ps.projectm.model.PushCalling
import com.nurbk.ps.projectm.others.AUTH_VALUE
import com.nurbk.ps.projectm.others.VALUE_TYPE
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationInterface {

    @Headers(
        "Authorization: key=${AUTH_VALUE}",
        "Content-Type:${VALUE_TYPE}"
    )
    @POST("send")
    fun sendRemoteMessage(
        @Body calling: PushCalling
    ): Call<ResponseBody>

}