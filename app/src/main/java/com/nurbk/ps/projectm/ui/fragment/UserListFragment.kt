package com.nurbk.ps.projectm.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.adapter.UserListAdapter
import com.nurbk.ps.projectm.databinding.FragmentUserListBinding
import com.nurbk.ps.projectm.model.CallingData
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.others.CALL_AUDIO
import com.nurbk.ps.projectm.others.CALL_VIDEO
import com.nurbk.ps.projectm.others.TYPE_CALL
import com.nurbk.ps.projectm.others.USER_DATA
import com.nurbk.ps.projectm.service.MessagingServiceFirebase
import com.nurbk.ps.projectm.ui.activity.MainActivity
import com.nurbk.ps.projectm.ui.dialog.LoadingDialog
import com.nurbk.ps.projectm.ui.viewmodel.IncomingCallViewModel
import com.nurbk.ps.projectm.ui.viewmodel.MainUserListViewModel
import com.nurbk.ps.projectm.utils.PreferencesManager


class UserListFragment : Fragment(), UserListAdapter.UserListener {

    private lateinit var mBinding: FragmentUserListBinding
    private lateinit var loadingDialog: LoadingDialog


    private val viewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[MainUserListViewModel::class.java]
    }

    private lateinit var user: User
    private val userAdapter by lazy {
        UserListAdapter(arrayListOf(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUserListBinding.inflate(inflater, container, false).apply {
            executePendingBindings()
        }
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog()
        viewModel.getProfile {
            user = PreferencesManager(requireContext()).getUserProfile()
            mBinding.txtNameUSer.text = user.name
        }
        viewModel.getToken {

        }




        mBinding.btnLogOut.setOnClickListener {
            loadingDialog.show(requireActivity().supportFragmentManager, "")
            viewModel.getLogOut().also {
                PreferencesManager(requireContext()).getEditor()!!.clear().clear().apply()
                requireActivity().finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }



        viewModel.getAllUserLiveData.observe(viewLifecycleOwner) {
            userAdapter.apply {
                userList.clear()
                userList.addAll(it)
                notifyDataSetChanged()
            }
        }

        mBinding.rcDataList.adapter = userAdapter


        if (requireActivity().intent.getBooleanExtra("d", false)) {
            findNavController().navigate(
                R.id.action_userListFragment_to_callFragment,
                Bundle().apply {
                    putParcelable(
                        USER_DATA,
                        requireActivity().intent.getParcelableExtra<CallingData>("data")
                    )
                    putString(TYPE_CALL, CALL_AUDIO)
                })

        }
    }


    override fun initiateVideoMeeting(user: User) {
        if (TextUtils.isEmpty(user.token)) {
            Snackbar.make(
                requireView(),
                "${user.name} is not available for meeting",
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            findNavController()
                .navigate(R.id.action_userListFragment_to_outgoingInvitationFragment,
                    Bundle().apply {
                        putParcelable(USER_DATA, user)
                        putString(TYPE_CALL, CALL_VIDEO)
                    })

        }
    }

    override fun initiateAudioMeeting(user: User) {
        if (TextUtils.isEmpty(user.token)) {
            Snackbar.make(
                requireView(),
                "${user.name} is not available for meeting",
                Snackbar.LENGTH_LONG
            ).show()
        } else {
            findNavController().navigate(
                R.id.action_userListFragment_to_outgoingInvitationFragment,
                Bundle().apply {
                    putParcelable(USER_DATA, user)
                    putString(TYPE_CALL, CALL_AUDIO)
                })

        }

    }


}