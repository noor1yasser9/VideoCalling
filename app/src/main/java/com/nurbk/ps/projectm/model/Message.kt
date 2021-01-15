package com.nurbk.ps.projectm.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import kotlinx.android.parcel.Parcelize

/**
 * Set messageId for recycler view adapter
 * Firebase timestamp is created after init method.
 */

@Parcelize
data class Message(
    var id: String = "",
    var senderId: String = "",
    var receiverId: String = "",
    var isOwner: Boolean = false,
    var name: String = "",
    var photoUrl: String = "",
    var audioUrl: String = "",
    var audioFile: String = "",
    var audioDuration: Long = 0,
    var text: String = "",
    var timestamp: Long = 0,
    var readTimestamp: Long = 0,
    var type: Int = 0
) : Parcelable {

    @get:Exclude
    var audioDownloaded = false

    fun setMessageId() {
        id = senderId + "_" + timestamp
    }

    fun getTime()= android.text.format.DateFormat.format("hh:mm a",timestamp)
}