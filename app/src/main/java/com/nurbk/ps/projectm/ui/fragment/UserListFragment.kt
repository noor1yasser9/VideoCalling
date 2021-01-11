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
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.ui.activity.MainActivity
import com.nurbk.ps.projectm.ui.dialog.LoadingDialog
import com.nurbk.ps.projectm.ui.viewmodel.MainUserListViewModel
import com.nurbk.ps.projectm.utils.PreferencesManager
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.Exception


class UserListFragment : Fragment(), UserListAdapter.UserListener {

    private lateinit var mBinding: FragmentUserListBinding
    private lateinit var loadingDialog: LoadingDialog


    private val viewModel by lazy {
        ViewModelProvider(
            this,
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
        loadingDialog = LoadingDialog()

        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



            viewModel.getProfile {

                try {
                    user =  PreferencesManager(requireContext()).getUserProfile()
                    mBinding.txtNameUSer.text=user.name
                } catch (e: Exception) {

                }


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

        loadingDialog.show(requireActivity().supportFragmentManager, "")

        viewModel.getAllUserLiveData.observe(viewLifecycleOwner) {
            userAdapter.apply {
                userList.clear()
                userList.addAll(it)
                notifyDataSetChanged()
                loadingDialog.dismiss()
            }
        }

        mBinding.rcDataList.adapter = userAdapter


    }


    override fun initiateVideoMeeting(user: User) {
        calling(user, CALL_VIDEO)
    }

    override fun initiateAudioMeeting(user: User) {
        calling(user, CALL_AUDIO)

    }

    private fun calling(user: User, typeCall: String) {
        if (TextUtils.isEmpty(user.token.trim())) {
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
                    putString(TYPE_CALL, typeCall)
                })
        }
    }

    override fun onItemClickListener(user: User) {
        findNavController()
            .navigate(R.id.action_userListFragment_to_chatFragment,
                Bundle().apply {
                    putParcelable(USER_DATA, user)
                })
    }

    private val invitationBroadcastManager = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val type = intent!!.getParcelableExtra<CallingData>("data")
            when (type!!.type) {
                REMOTE_MSG_INVITATION -> {
                    findNavController().navigate(
                        R.id.action_userListFragment_to_callFragment,
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
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(invitationBroadcastManager)
    }

    override fun onMultipleUserAction(isMultipleUserSelected: Boolean) {
        if (isMultipleUserSelected) {
            mBinding.sendGroup.visibility = View.VISIBLE
            mBinding.sendGroup.setOnClickListener {
                findNavController().navigate(
                    R.id.action_userListFragment_to_outgoingInvitationFragment,
                    Bundle().apply {
                        putParcelable(USER_DATA, user)
                        putString(TYPE_CALL, CALL_AUDIO)
                    })
            }

        } else
            mBinding.sendGroup.visibility = View.GONE

    }
}