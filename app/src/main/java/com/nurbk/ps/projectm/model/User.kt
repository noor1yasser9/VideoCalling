package com.nurbk.ps.projectm.model

import android.os.Parcelable
import com.nurbk.ps.projectm.others.USER_PROFILE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val image: String = "",
    val token: String = "",

    var isOnline: Boolean = false,
    var typeUser: Int = USER_PROFILE,
    var usersGroup: ArrayList<User> = arrayListOf()
) : Parcelable