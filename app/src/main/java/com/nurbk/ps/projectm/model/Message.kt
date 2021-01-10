package com.nurbk.ps.projectm.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

/**
 * Set messageId for recycler view adapter
 * Firebase timestamp is created after init method.
 */

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
    var type: Int=0
) {

    @get:Exclude
    var audioDownloaded = false

    fun setMessageId() {
            id = senderId + "_" + timestamp
    }
}