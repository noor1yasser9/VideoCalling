package com.nurbk.ps.projectm.ui.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.facebook.FacebookSdk.getApplicationContext
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.FragmentVideoChatViewBinding
import com.nurbk.ps.projectm.model.CallingData
import com.nurbk.ps.projectm.model.PushCalling
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.network.ApiClient
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.utils.PreferencesManager
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import io.agora.rtc.video.VideoEncoderConfiguration
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class VideoCallingFragment : Fragment() {

    private val TAG: String = VideoCallingFragment::class.java.simpleName
    private val PERMISSION_REQ_ID = 22


    private var mMuted = false
    private lateinit var user: User


    private val argumentData by lazy {
        requireArguments()
    }
    private val REQUESTED_PERMISSIONS = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    private lateinit var mBinding: FragmentVideoChatViewBinding
    private lateinit var dataCalling: CallingData;

    private var mLocalVideo: VideoCanvas? = null
    private var mRemoteVideo: VideoCanvas? = null
    private var mRtcEngine: RtcEngine? = null
    var video = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentVideoChatViewBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }
        user = PreferencesManager(requireContext()).getUserProfile()

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataCalling = argumentData.getParcelable(DATA_CALLING)!!
        if (checkSelfPermission(
                REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID
            ) &&
            checkSelfPermission(
                REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID
            ) &&
            checkSelfPermission(
                REQUESTED_PERMISSIONS[2], PERMISSION_REQ_ID
            )
        ) {
            initUI()
            initEngineAndJoinChannel()
        }
    }


    private fun initUI() {

        mBinding.btnCall.setOnClickListener {


            PushCalling(
                CallingData(
                    type = FINISH_CALL
                ),
                if (user.token == dataCalling.senderToken) dataCalling.receiverToken else dataCalling.senderToken
            ).also {
                ApiClient(requireContext()).notificationInterface
                    .sendRemoteMessage(
                        it
                    ).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {

                                findNavController().navigateUp()
                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        }
                    })

            }
        }
        mBinding.btnSwitchCamera.setOnClickListener {
            onSwitchCameraClicked()
        }
        mBinding.btnMute.setOnClickListener {
            onLocalAudioMuteClicked()
        }
        mBinding.localVideoViewContainer.setOnClickListener {
            onLocalContainerClick()
        }

         video = dataCalling.meetingType != CALL_AUDIO
        mBinding.videoStop.isVisible =!video
        mBinding.btnStartCamera.setOnClickListener {
            video = !video
            mRtcEngine!!.enableLocalVideo(video)

        }

    }

    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) = Unit


        override fun onFirstRemoteVideoDecoded(
            uid: Int,
            width: Int,
            height: Int,
            elapsed: Int
        ) {
            requireActivity().runOnUiThread {
                setupRemoteVideo(uid)
            }
        }


        override fun onUserOffline(uid: Int, reason: Int) {
            requireActivity().runOnUiThread { //
                onRemoteUserLeft(uid)
            }

        }

        override fun onUserEnableLocalVideo(uid: Int, enabled: Boolean) {
            super.onUserEnableLocalVideo(uid, enabled)
            requireActivity().runOnUiThread {
                mBinding.videoStop.isVisible = !enabled
            }

        }
    }


    private fun setupRemoteVideo(uid: Int) {
        var parent: ViewGroup = mBinding.remoteVideoViewContainer
        if (parent.indexOfChild(mLocalVideo!!.view) > -1) {
            parent = mBinding.localVideoViewContainer
        }

        if (mRemoteVideo != null) {
            return
        }


        val view = RtcEngine.CreateRendererView(requireContext())
        view.setZOrderMediaOverlay(parent === mBinding.localVideoViewContainer)
        parent.addView(view)
        mRemoteVideo = VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, uid)
        mRtcEngine!!.setupRemoteVideo(mRemoteVideo)
    }

    private fun onRemoteUserLeft(uid: Int) {
        if (mRemoteVideo != null && mRemoteVideo!!.uid == uid) {
            removeFromParent(mRemoteVideo)
            mRemoteVideo = null
        }
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) !==
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUESTED_PERMISSIONS,
                requestCode
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED
                || grantResults[1] != PackageManager.PERMISSION_GRANTED
                || grantResults[2] != PackageManager.PERMISSION_GRANTED
            ) {
                showLongToast(
                    "Need permissions " + Manifest.permission.RECORD_AUDIO +
                            "/" + Manifest.permission.CAMERA + "/" + Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                findNavController().navigateUp()
                return
            }

            initEngineAndJoinChannel()
        }
    }


    private fun showLongToast(msg: String) {
        Toast.makeText(
            getApplicationContext(),
            msg,
            Toast.LENGTH_LONG
        ).show()

    }


    private fun initEngineAndJoinChannel() {

        initializeEngine()
        setupVideoConfig()
        setupLocalVideo()
        joinChannel()
    }

    private fun initializeEngine() {
        mRtcEngine = try {
            RtcEngine.create(
                requireContext(),
                getString(R.string.agora_app_id),
                mRtcEventHandler
            )
        } catch (e: Exception) {
            Log.e(TAG, Log.getStackTraceString(e))
            throw RuntimeException(
                """
                    NEED TO check rtc sdk init fatal error
                    ${Log.getStackTraceString(e)}
                    """.trimIndent()
            )
        }
    }

    private fun setupVideoConfig() {
        mRtcEngine!!.enableVideo()
        mRtcEngine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT
            )
        )
    }

    private fun setupLocalVideo() {
        val view = RtcEngine.CreateRendererView(requireContext())
        view.setZOrderMediaOverlay(true)
        mBinding.localVideoViewContainer.addView(view)
        mLocalVideo = VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 0)
        mRtcEngine!!.setupLocalVideo(mLocalVideo)
    }

    private fun joinChannel() {
        var token: String? = getString(R.string.agora_access_token)
        if (TextUtils.isEmpty(token) || TextUtils.equals(
                token,
                dataCalling.meetingRoom
            )
        ) {
            token = null // default, no token
        }
        mRtcEngine!!.enableLocalVideo(dataCalling.meetingType != CALL_AUDIO);
        mRtcEngine!!.joinChannel(token, dataCalling.meetingRoom, "Extra Optional Data", 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        Thread {
            leaveChannel()
            RtcEngine.destroy()
        }.start()

    }

    private fun leaveChannel() {
        mRtcEngine!!.leaveChannel()
    }

    private fun onLocalAudioMuteClicked() {
        mMuted = !mMuted
        mRtcEngine!!.muteLocalAudioStream(mMuted)
        val res = if (mMuted) R.drawable.btn_mute else R.drawable.btn_unmute
        mBinding.btnMute.setImageResource(res)
    }

    private fun onSwitchCameraClicked() {
        mRtcEngine!!.switchCamera()
    }

    private fun removeFromParent(canvas: VideoCanvas?): ViewGroup? {
        if (canvas != null) {
            val parent = canvas.view.parent
            if (parent != null) {
                val group = parent as ViewGroup
                group.removeView(canvas.view)
                return group
            }
        }
        return null
    }

    private fun switchView(canvas: VideoCanvas) {
        val parent = removeFromParent(canvas)
        if (parent === mBinding.localVideoViewContainer) {
            if (canvas.view is SurfaceView) {
                (canvas.view as SurfaceView).setZOrderMediaOverlay(false)
            }
            mBinding.remoteVideoViewContainer.addView(canvas.view)
        } else if (parent === mBinding.remoteVideoViewContainer) {
            if (canvas.view is SurfaceView) {
                (canvas.view as SurfaceView).setZOrderMediaOverlay(true)
            }
            mBinding.localVideoViewContainer.addView(canvas.view)
        }
    }

    private fun onLocalContainerClick() {
        switchView(mLocalVideo!!)
        switchView(mRemoteVideo!!)
    }


    private val invitationBroadcastManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getParcelableExtra<CallingData>("data")
            when (type!!.type) {
                FINISH_CALL -> {
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