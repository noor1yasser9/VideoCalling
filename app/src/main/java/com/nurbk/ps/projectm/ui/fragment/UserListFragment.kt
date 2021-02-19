package com.nurbk.ps.projectm.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.adapter.UserListAdapter
import com.nurbk.ps.projectm.databinding.FragmentUserListBinding
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.model.modelNetwork.CallingData
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.ui.activity.MainActivity
import com.nurbk.ps.projectm.ui.dialog.LoadingDialog
import com.nurbk.ps.projectm.ui.viewmodel.MainUserListViewModel
import com.nurbk.ps.projectm.utils.PreferencesManager
import java.lang.Exception
import java.util.*


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


        viewModel.updateUser(
            mapOf("isOnline" to true), FirebaseAuth.getInstance().uid!!,
            COLLECTION_USERS
        ) {

        }
        viewModel.getProfile {

            try {
                user = PreferencesManager(requireContext()).getUserProfile()
                mBinding.txtNameUSer.text = user.name
            } catch (e: Exception) {

            }


        }

        viewModel.getToken {

        }

        mBinding.btnLogOut.setOnClickListener {
            loadingDialog.show(requireActivity().supportFragmentManager, "")
            requireActivity()
                .getSharedPreferences(NAME_FILE_PREF, Context.MODE_PRIVATE)
                .edit().putBoolean(IS_SIGN_IN, false).apply()
            viewModel.updateUser(
                mapOf("isOnline" to false), FirebaseAuth.getInstance().uid!!,
                COLLECTION_USERS
            ) {

            }
            viewModel.getLogOut().also {
                PreferencesManager(requireContext()).getEditor()!!.clear().apply()
                requireActivity().finish()
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }

        loadingDialog.show(requireActivity().supportFragmentManager, "")

        viewModel.getAllUserLiveData.observe(viewLifecycleOwner) {
            viewModel.getAllGroupLiveData.observe(viewLifecycleOwner) { groups ->
                userAdapter.apply {
                    userList.clear()
                    userList.addAll(it)
                    userList.addAll(groups)
                    notifyDataSetChanged()
                    loadingDialog.dismiss()
                }
            }

            mBinding.rcDataList.adapter = userAdapter
            dataNotification(it)
        }

        mBinding.sendGroup.setOnClickListener {
            findNavController().navigate(
                R.id.action_userListFragment_to_outgoingInvitationFragment,
                Bundle().apply {
                    putParcelableArrayList(USER_DATA_LIST, userAdapter.userSelected)
                    putString(TYPE_CALL, CALL_AUDIO)
                    putString(TYPE, TYPE_GROUP)
                })
            userAdapter.userSelected.clear()
        }

        mBinding.createGroup.setOnClickListener {
            userAdapter.userSelected.add(user)
            userAdapter.userSelected.forEach {
                viewModel.addGroup(
                    it.id,
                    User(
                        id = UUID.randomUUID().toString(),
                        name = "ASdfas",
                        usersGroup = userAdapter.userSelected,
                        typeUser = USER_GROUP,
                    )
                )
            }
            userAdapter.userSelected.clear()
            mBinding.rcDataList.adapter = userAdapter
        }
    }


    override fun initiateVideoMeeting(user: User) {
        calling(user, CALL_VIDEO)
    }

    override fun initiateAudioMeeting(user: User) {
        calling(user, CALL_AUDIO)

    }

    override fun onMultipleUserAction(isMultipleUserSelected: Boolean) {
        if (isMultipleUserSelected)
            mBinding.group.visibility = View.VISIBLE
        else
            mBinding.group.visibility = View.GONE

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
                    putString(TYPE, TYPE_SINGLE)
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

    private fun dataNotification(it: List<User>) {
        if (requireActivity().intent.getParcelableExtra<Message>(USER_DATA_LIST) != null) {
            val message = requireActivity().intent.getParcelableExtra<Message>(USER_DATA_LIST)
            it.find { user ->
                user.id == message!!.senderId
            }.also {
                findNavController()
                    .navigate(R.id.action_userListFragment_to_chatFragment,
                        Bundle().apply {
                            putParcelable(USER_DATA, it)
                        })
                requireActivity().intent.removeExtra(USER_DATA_LIST)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(invitationBroadcastManager)

    }

    override fun onDestroy() {
        viewModel.updateUser(
            mapOf("isOnline" to false), user.id,
            COLLECTION_USERS
        ) {

        }
        super.onDestroy()
    }


}