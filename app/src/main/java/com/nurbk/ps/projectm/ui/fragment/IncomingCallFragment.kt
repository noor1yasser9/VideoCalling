package com.nurbk.ps.projectm.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.FragmentCallIncomingBinding
import com.nurbk.ps.projectm.model.CallingData
import com.nurbk.ps.projectm.model.PushCalling
import com.nurbk.ps.projectm.network.ApiClient
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.ui.activity.MainActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncomingCallFragment : Fragment() {

    private lateinit var mBinding: FragmentCallIncomingBinding
    private lateinit var dataCalling: CallingData;
    private val argumentData by lazy {
        requireArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCallIncomingBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataCalling = argumentData.getParcelable<CallingData>(USER_DATA)!!
        mBinding.data = dataCalling

        if (dataCalling.meetingType == CALL_AUDIO)
            mBinding.imageTypeCall.setImageResource(R.drawable.ic_baseline_call_24)

        mBinding.btnFinshCall.setOnClickListener {
            sendRemoteMessage(false, REMOTE_MSG_INVITATION_REJECTED)
            findNavController().navigateUp()
        }
        mBinding.btnStartCall.setOnClickListener {
            sendRemoteMessage(true, REMOTE_MSG_INVITATION_ACCEPTED)
        }
    }


    private fun sendRemoteMessage(acceptOrReject: Boolean, type: String) {
        PushCalling(
            CallingData(
                name = dataCalling.name, meetingType = dataCalling.meetingType,
                type = type, email = dataCalling.email,
                acceptedOrRejected = acceptOrReject,
                senderToken = dataCalling.receiverToken,
                receiverToken = dataCalling.senderToken
            ),
            dataCalling.senderToken
        ).also {
            ApiClient(requireContext()).notificationInterface
                .sendRemoteMessage(
                    it
                ).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful)
                            Log.e("ttttttttttttisSucc", response.body().toString())
                        else {
                            Log.e("tttttttttttt", response.errorBody().toString())

                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                    }
                })
        }

    }

    private val invitationBroadcastManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getParcelableExtra<CallingData>("data")
            when (type!!.type) {
                REMOTE_MSG_INVITATION_ACCEPTED -> {

                }
                REMOTE_MSG_INVITATION_REJECTED -> {
                    findNavController().navigateUp()
                }
                REMOTE_MSG_INVITATION_CANCEL -> {
                    findNavController().navigateUp()
                }
            }
            Log.e("tttttttttIn","InCome")
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(
                invitationBroadcastManager,
                IntentFilter(REMOTE_MSG_INVITATION_RESPONSE)
            )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(invitationBroadcastManager)
    }

}