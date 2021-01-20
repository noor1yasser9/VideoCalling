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
import com.nurbk.ps.projectm.model.modelNetwork.CallingData
import com.nurbk.ps.projectm.model.modelNetwork.PushCalling
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.network.ApiClient
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.utils.PreferencesManager
import okhttp3.ResponseBody
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*


class OutgoingInvitationFragment : Fragment() {

    private lateinit var mBinding: FragmentOutgoingInvitationBinding
    private lateinit var argumentData: Bundle
    private lateinit var user: User
    private var typeMeeting = CALL_VIDEO
    private var meetingRoom = ""
    private var isAudio = false
    private lateinit var dataCalling: CallingData

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

    private lateinit var usersList: ArrayList<User>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        argumentData = requireArguments()
        if (argumentData.getString(TYPE) == TYPE_SINGLE) {
            user = argumentData.getParcelable(USER_DATA)!!
            sendRemoteMessage(REMOTE_MSG_INVITATION, user)
            mBinding.user = user
        } else {
            usersList = argumentData.getParcelableArrayList(USER_DATA_LIST)!!

            usersList.forEach {
                sendRemoteMessage(REMOTE_MSG_INVITATION, it)
                mBinding.user = it
            }

        }


        if (argumentData.getString(TYPE_CALL) == CALL_AUDIO) {
            isAudio = true
            mBinding.imageTypeCall.setImageResource(R.drawable.ic_baseline_call_24)
            typeMeeting = CALL_AUDIO
        }
        mBinding.btnCancelCall.setOnClickListener {
            if (argumentData.getString(TYPE) == TYPE_SINGLE)
                sendRemoteMessage(REMOTE_MSG_INVITATION_CANCEL, user)
            else
                usersList.forEach {
                    sendRemoteMessage(REMOTE_MSG_INVITATION, it)
                }
            findNavController().navigateUp()
        }


    }

    private fun sendRemoteMessage(type: String, user: User) {
        meetingRoom = userProfile.id + "_" + UUID.randomUUID().toString().substring(0, 5)
        dataCalling = CallingData(
            name = userProfile.name, meetingType = typeMeeting,
            type = type, email = userProfile.email,
            senderToken = userProfile.token,
            receiverToken = user.token,
            meetingRoom = meetingRoom,
            typeMeeting = argumentData.getString(TYPE)!!
        )
        PushCalling(
            dataCalling,
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
                            Log.e("ttttttttSuccessful", response.body()!!.string())
                        else
                            Log.e("ttttt",response.errorBody()!!.string())
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        }

    }

    private val invitationBroadcastManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getParcelableExtra<CallingData>("data")
            when (type!!.type) {
                REMOTE_MSG_INVITATION_ACCEPTED -> {

                    if (argumentData.getString(TYPE) == TYPE_SINGLE) {
                        val bundle = Bundle()
                        bundle.putParcelable(DATA_CALLING, dataCalling)
                        findNavController().navigate(R.id.action_to_call, bundle)
                    } else {
                        val options = JitsiMeetConferenceOptions.Builder()
                            .setServerURL(URL("https://meet.jit.si"))
                            .setRoom(dataCalling.meetingRoom)
                            .setAudioMuted(false)
                            .setVideoMuted(false)
                            .setAudioOnly(false)
                            .setWelcomePageEnabled(false)
                            .build()
                        JitsiMeetActivity.launch(requireContext(), options)
                        findNavController().navigateUp()
                    }

                }
                REMOTE_MSG_INVITATION_REJECTED -> {
                    findNavController().navigateUp()
                }
                REMOTE_MSG_INVITATION_CANCEL -> {
                    findNavController().navigateUp()
                }
            }
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