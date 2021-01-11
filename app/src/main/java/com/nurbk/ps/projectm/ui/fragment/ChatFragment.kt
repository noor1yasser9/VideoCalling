package com.nurbk.ps.projectm.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.devlomi.record_view.OnRecordListener
import com.google.android.material.snackbar.Snackbar
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.adapter.MessageAdapter
import com.nurbk.ps.projectm.databinding.FragmentChatBinding
import com.nurbk.ps.projectm.model.CallingData
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.ui.dialog.LoadingDialog
import com.nurbk.ps.projectm.ui.viewmodel.ChatViewModel
import com.nurbk.ps.projectm.utils.AudioHelper
import com.nurbk.ps.projectm.utils.FileHelper
import com.nurbk.ps.projectm.utils.PreferencesManager
import com.nurbk.ps.projectm.utils.bindingFakeAudioProgress
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {


    private lateinit var mBinding: FragmentChatBinding
    private lateinit var userRec: User
    private lateinit var user: User
    private lateinit var audioPath: String
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var fileHelper: FileHelper
    private lateinit var audioHelper: AudioHelper
    private lateinit var loadingDialog: LoadingDialog

    private var id = ""
    private var recordTime: Long = 0
    var isShow = false

    private val intent by lazy {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }
    private val argumentData by lazy {
        requireArguments()
    }
    private val adapterMassage by lazy {
        MessageAdapter(getMsgAdapterListener())
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChatBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fileHelper = FileHelper(requireContext())
        audioHelper = AudioHelper(fileHelper, activity as AppCompatActivity)

        loadingDialog = LoadingDialog()
        loadingDialog.show(requireActivity().supportFragmentManager, "")

        userRec = argumentData.getParcelable(USER_DATA)!!
        user = PreferencesManager(requireContext()).getUserProfile()

        mBinding.recyclerViewMessage.apply {
            adapter = adapterMassage
        }

        mBinding.txtNameUSer.text = userRec.name

        viewModel.createChatChannel(userRec.id) { id ->
            viewModel.getAllMessage(id)
            this.id = id

        }
        viewModel.getTyping(user.id, userRec.id)
        viewModel.typing.observe(viewLifecycleOwner) {
            mBinding.typingStatus.isVisible = it
        }

        viewModel.getMessageLiveData().observe(viewLifecycleOwner) {
            adapterMassage.dataList = it as ArrayList<Message>
            adapterMassage.notifyDataSetChanged()
            if (loadingDialog.isAdded)
                loadingDialog.dismiss()
        }
        mBinding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.msgText.doAfterTextChanged { text ->
            if (text.toString().trim { it <= ' ' }.isEmpty()) {
                mBinding.btnSend.visibility = View.GONE
                mBinding.recordButton.visibility = View.VISIBLE
                viewModel.updateCollection(mapOf("typing" to false), userRec.id, user.id)
            } else {
                mBinding.recordButton.visibility = View.GONE
                mBinding.btnSend.visibility = View.VISIBLE
                viewModel.updateCollection(mapOf("typing" to true), userRec.id, user.id)
            }
        }

        checkPermission()
        mBinding.recordButton.setRecordView(mBinding.recordView)
        mBinding.recordButton.setOnClickListener {
            permission(requireContext(), arrayListOf(Manifest.permission.RECORD_AUDIO)) {
                mBinding.recordButton.isListenForRecord = true
            }
        }

        mBinding.btnSend.setOnClickListener {
            val message = Message(
                name = user.name,
                type = TYPE_MESSAGE_TEXT,
                audioDuration = 0,
                audioFile = "",
                audioUrl = "",
                receiverId = user.id,
                photoUrl = "",
                senderId = user.id,
                text = mBinding.msgText.text.toString().trim(),
                timestamp = Calendar.getInstance().time.time,
            )
            viewModel.sendMessage(id, message)
            mBinding.msgText.text.clear()

        }



        mBinding.btnDataSend.setOnClickListener {
            if (!isShow)
                mBinding.layoutRes.visibility = View.VISIBLE
            else
                mBinding.layoutRes.visibility = View.GONE

            isShow = !isShow
        }
        mBinding.btnImage.setOnClickListener {
            permission(
                requireContext(),
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                selectImage()
            }
            mBinding.layoutRes.visibility = View.GONE
        }

        mBinding.btnVideo.setOnClickListener {
            permission(
                requireContext(),
                arrayListOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                selectVideo()
            }
            mBinding.layoutRes.visibility = View.GONE
        }

        mBinding.recordView.setOnRecordListener(object : OnRecordListener {
            override fun onStart() {
                setUpRecording()
                try {
                    mediaRecorder.prepare()
                    mediaRecorder.start()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                mBinding.messageLayout.visibility = View.GONE
                mBinding.recordView.visibility = View.VISIBLE
            }

            override fun onCancel() {
                mediaRecorder.reset()
                mediaRecorder.release()
                val file = File(audioPath)
                if (file.exists()) file.delete()
                mBinding.recordView.visibility = View.GONE
                mBinding.messageLayout.visibility = View.VISIBLE
            }

            override fun onFinish(recordTime: Long) {
                this@ChatFragment.recordTime = recordTime
                try {
                    mediaRecorder.stop()
                    mediaRecorder.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                mBinding.recordView.visibility = View.GONE
                mBinding.messageLayout.visibility = View.VISIBLE
                sendRecodingMessage(audioPath)
            }

            override fun onLessThanSecond() {
                mediaRecorder.reset()
                mediaRecorder.release()
                val file = File(audioPath)
                if (file.exists()) file.delete()
                mBinding.recordView.visibility = View.GONE
                mBinding.messageLayout.visibility = View.VISIBLE
            }

        })

        mBinding.btnCallAudio.setOnClickListener {
            calling(CALL_AUDIO)
        }

        mBinding.btnCallVideo.setOnClickListener {
            calling(CALL_VIDEO)
        }
    }


    private fun calling(typeCall: String) {
        if (TextUtils.isEmpty(userRec.token.trim())) {
            Snackbar.make(
                requireView(),
                "${userRec.name} is not available for meeting",
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            findNavController().navigate(
                R.id.action_chatFragment_to_outgoingInvitationFragment,
                Bundle().apply {
                    putParcelable(USER_DATA, userRec)
                    putString(TYPE_CALL, typeCall)
                })
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CODE &&
            resultCode == Activity.RESULT_OK
        ) {

            viewModel.uploadImage(compressFormat(data!!.data!!, requireActivity())) {
                val message = Message(
                    name = user.name,
                    type = TYPE_MESSAGE_IMAGE,
                    audioDuration = 0,
                    audioFile = "",
                    audioUrl = "",
                    receiverId = user.id,
                    photoUrl = it,
                    senderId = user.id,
                    text = "",
                    timestamp = Calendar.getInstance().time.time,
                )

                viewModel.sendMessage(id, message)
            }
            isShow = false
        } else if (requestCode == REQUEST_IMAGE_VIDEO &&
            resultCode == Activity.RESULT_OK
        ) {


            viewModel.uploadVideo(data!!.data!!, {
                val message = Message(
                    name = user.name,
                    type = TYPE_MESSAGE_VIDEO,
                    audioDuration = 0,
                    audioFile = "",
                    audioUrl = "",
                    receiverId = user.id,
                    photoUrl = it,
                    senderId = user.id,
                    text = "",
                    timestamp = Calendar.getInstance().time.time,
                )

                viewModel.sendMessage(id, message)
            }, {

            })
        }


    }


    private var filePath = ""
    private fun setUpRecording() {
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        filePath = requireActivity().filesDir.path
        val file = File(filePath)
        if (!file.exists()) file.mkdirs()
        audioPath = "$file/${System.currentTimeMillis()}.mp3"
        mediaRecorder.setOutputFile(audioPath)
    }


    private fun sendRecodingMessage(audioPath: String) {
        viewModel.uploadUri(id, user.id, audioPath) { uri ->
            val message = Message(
                name = user.name,
                type = TYPE_MESSAGE_RECORD,
                audioDuration = recordTime,
                audioFile = audioPath,
                audioUrl = uri,
                receiverId = user.id,
                photoUrl = "",
                senderId = user.id,
                text = "",
                timestamp = Calendar.getInstance().time.time,
            )

            viewModel.sendMessage(id, message)
        }
    }


    private fun getMsgAdapterListener(): MessageAdapter.MsgAdapterListener {
        return object : MessageAdapter.MsgAdapterListener {
            override fun showPic(view: View, message: Message) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar, item: Message) {
                val parentView = seekBar.parent as ConstraintLayout
                val playButton = parentView.findViewById<View>(R.id.playButton)
                if (playButton.isActivated) audioHelper.stopTimer()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar, item: Message) {
                val parentView = seekBar.parent as ConstraintLayout
                val playButton = parentView.findViewById<View>(R.id.playButton) ?: return
                if (playButton.isActivated) {
                    audioHelper.stopTimer()
                    audioHelper.stopPlaying()
                    audioHelper.startPlaying()
                } else {
                    val audioTimeView =
                        parentView.findViewById<TextView>(R.id.audioTimeTextView)
                    val audioDuration = item.audioDuration
                    audioHelper.setAudioTime(seekBar, audioTimeView, audioDuration)
                }
            }

            override fun onAudioClick(view: View, message: Message) {
                if (message.audioDownloaded.not()) bindingFakeAudioProgress(view, message)
                else audioHelper.setupAudioHelper(view, message)
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            mBinding.recordButton.isListenForRecord = false
        }
    }


    override fun onDestroy() {

        adapterMassage.dataList.clear()
        super.onDestroy()
    }


    private fun selectImage() {
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_CODE)
    }

    private fun selectVideo() {
        intent.type = "video/*"
        startActivityForResult(intent, REQUEST_IMAGE_VIDEO)

    }


    private val invitationBroadcastManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getParcelableExtra<CallingData>("data")
            when (type!!.type) {
                REMOTE_MSG_INVITATION -> {
                    findNavController().navigate(
                        R.id.action_chatFragment_to_callFragment2,
                        Bundle().apply {
                            putParcelable(
                                USER_DATA,
                                type
                            )
                            putString(TYPE_CALL, CALL_AUDIO)
                        })
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
        viewModel.updateCollection(mapOf("typing" to false), userRec.id, user.id)
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(invitationBroadcastManager)
    }


}
