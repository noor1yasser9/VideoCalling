package com.nurbk.ps.projectm.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            typeMeeting=CALL_AUDIO
        }
        mBinding.btnCancelCall.setOnClickListener {
            findNavController().navigateUp()
        }
        sendRemoteMessage()

    }

    private fun sendRemoteMessage() {
        PushCalling(
            CallingData(
                name = userProfile.name, meetingType = typeMeeting,
                type = REMOTE_MSG_INVITATION, email = userProfile.email,
                inviterToken = userProfile.token
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


}