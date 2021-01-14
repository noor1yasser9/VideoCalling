package com.nurbk.ps.projectm.model

import android.os.Parcelable
import com.nurbk.ps.projectm.others.TYPE_SINGLE
import kotlinx.android.parcel.Parcelize

@Parcelize
class CallingData(
    val type: String = "",
    val invitation: String = "",
    val meetingType: String = "",
    val senderToken: String = "",
    val receiverToken: String = "",
    val data: String = "",
    val registration_ids: String = "",
    val name: String = "",
    val email: String = "",
    val meetingRoom: String = "",
    val acceptedOrRejected: Boolean = false,
    val typeMeeting: String = TYPE_SINGLE
) : Parcelable

