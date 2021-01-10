package com.nurbk.ps.projectm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.ItemUserListBinding
import com.nurbk.ps.projectm.model.User

class UserListAdapter(val userList: ArrayList<User>, val userListener: UserListener) :
    RecyclerView.Adapter<UserListAdapter.UserListViewHolder>() {

    val userSelected = ArrayList<User>()

    inner class UserListViewHolder(val mBinding: ItemUserListBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(user: User) {
            mBinding.user = user
            mBinding.btnCallAudio.setOnClickListener {
                userListener.initiateAudioMeeting(user)
            }
            mBinding.btnCallvideo.setOnClickListener {
                userListener.initiateVideoMeeting(user)
            }

            mBinding.cardRoot.setOnLongClickListener {
                userSelected.add(user)
                mBinding.imageSelected.visibility = View.VISIBLE
                mBinding.group.visibility = View.INVISIBLE
                userListener.onMultipleUserAction(true)
                true
            }
            mBinding.cardRoot.setOnClickListener {
                if (mBinding.imageSelected.isVisible) {
                    userSelected.remove(user)
                    mBinding.imageSelected.visibility = View.GONE
                    mBinding.group.visibility = View.VISIBLE
                    if (userSelected.size == 0)
                        userListener.onMultipleUserAction(false)
                } else {
                    if (userSelected.size > 0) {
                        userSelected.add(user)
                        mBinding.imageSelected.visibility = View.VISIBLE
                        mBinding.group.visibility = View.INVISIBLE

                    }

                }
            }

            mBinding.root.setOnClickListener {
                userListener.onItemClickListener(user)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListViewHolder {
        return UserListViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_user_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserListViewHolder, position: Int) {
        holder.bind(user = userList[position])
    }

    override fun getItemCount() = userList.size

    interface UserListener {
        fun initiateVideoMeeting(user: User)
        fun initiateAudioMeeting(user: User)
        fun onMultipleUserAction(isMultipleUserSelected: Boolean)
        fun onItemClickListener(user: User)
    }
}