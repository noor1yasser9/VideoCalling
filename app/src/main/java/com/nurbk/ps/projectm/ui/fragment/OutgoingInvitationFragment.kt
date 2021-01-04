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
import com.nurbk.ps.projectm.databinding.FragmentOutgoingInvitationBinding
import com.nurbk.ps.projectm.model.CallingData
import com.nurbk.ps.projectm.model.PushCalling
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.network.ApiClient
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.utils.PreferencesManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OutgoingInvitationFragment : Fragment() {

    private lateinit var mBinding: FragmentOutgoingInvitationBinding
    private lateinit var argumentData: Bundle
    private lateinit var user: User
    private var typeMeeting = CALL_VIDEO
    private val userProfile by lazy {
        PreferencesManager(requireContext()).getUserProfile()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentOutgoingInvitationBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        argumentData = requireArguments()
        user = argumentData.getParcelable(USER_DATA)!!
        mBinding.user = user

        if (argumentData.getString(TYPE_CALL) == CALL_AUDIO) {
            mBinding.imageTypeCall.setImageResource(R.drawable.ic_baseline_call_24)
            typeMeeting = CALL_AUDIO
        }
        mBinding.btnCancelCall.setOnClickListener {
            sendRemoteMessage(REMOTE_MSG_INVITATION_CANCEL)
            findNavController().navigateUp()
        }
        sendRemoteMessage(REMOTE_MSG_INVITATION)

    }

    private fun sendRemoteMessage(type: String) {
        PushCalling(
            CallingData(
                name = userProfile.name, meetingType = typeMeeting,
                type = type, email = userProfile.email,
                senderToken = userProfile.token,
                receiverToken = user.token
            ),
            user.token
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

            Log.e("tttttttttIn","Outgoing")

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