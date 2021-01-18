package com.nurbk.ps.projectm.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.others.COLLECTION_CHAT_CHANNEL
import com.nurbk.ps.projectm.others.COLLECTION_CHAT_MESSAGE
import com.nurbk.ps.projectm.others.COLLECTION_USERS
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ChatRepository private constructor(val context: Context) {


    companion object {
        @Volatile
        private var instance: ChatRepository? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                instance ?: createRepository(context).also {
                    instance = it
                }
            }

        private fun createRepository(context: Context) =
            ChatRepository(context)
    }

    fun sentMessage(
        channelId: String,
        message: Message,
    ) =
        FirebaseFirestore.getInstance().collection(COLLECTION_CHAT_MESSAGE)
            .document(channelId)
            .apply {
                set(mapOf("typing" to false))
            }
            .collection(COLLECTION_CHAT_MESSAGE)
            .add(message)

    private val newChatChannel =
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS).document()

    fun createChatChannel(
        uid: String,
        onComplete: (channelId: String) -> Unit
    ) =
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection(COLLECTION_CHAT_CHANNEL)
            .document(uid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                } else {
                    sendChatUid(uid, FirebaseAuth.getInstance().currentUser!!.uid)
                        .set(
                            mapOf(
                                "channelId" to newChatChannel.id,
                                "typing" to false,
                            )
                        )
                    sendChatUid(FirebaseAuth.getInstance().currentUser!!.uid, uid)
                        .set(
                            mapOf(
                                "channelId" to newChatChannel.id,
                                "typing" to false,
                            )
                        )
                    onComplete(newChatChannel.id)

                }
            }

    private val _typing = MutableLiveData<Boolean>()
    val typingLiveData: LiveData<Boolean> = _typing
    fun getTyping(id: String, idRes: String) {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_USERS)
            .document(id)
            .collection(COLLECTION_CHAT_CHANNEL)
            .document(idRes)
            .addSnapshotListener { querySnapshot, _ ->
                _typing.postValue(querySnapshot!!.data!!["typing"] as Boolean)
            }
    }

    fun updateCollection(
        data: Map<String, Any>,
        id: String, idRes: String
    ) = FirebaseFirestore
        .getInstance()
        .collection(COLLECTION_USERS)
        .document(id)
        .collection(COLLECTION_CHAT_CHANNEL)
        .document(idRes)
        .update(
            data
        ).addOnCompleteListener {
//            if (it.isSuccessful) {
//                updateLiveData.postValue(true)
//                onComplete()
//            } else {
//                updateLiveData.postValue(false)
//            }
        }


    private fun sendChatUid(
        senderName: String,
        recipientName: String
    ) =
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
            .document(senderName)
            .collection(COLLECTION_CHAT_CHANNEL)
            .document(recipientName)


    private val _getChat = MutableLiveData<List<Message>>()

    val getChat: LiveData<List<Message>>
        get() = _getChat


    fun getAllMessage(channelId: String) {
        val array = ArrayList<Message>()
        FirebaseFirestore.getInstance().collection(COLLECTION_CHAT_MESSAGE)
            .document(channelId)
            .collection(COLLECTION_CHAT_MESSAGE)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, _ ->
                array.clear()
                querySnapshot?.documents!!.forEach {

                    val item = it.toObject(Message::class.java)
                    array.add(item!!)
                }

                _getChat.postValue(array)
            }
    }


    fun uploadUri(
        idChat: String,
        userId: String,
        audioPath: String,
        onSuccess: (imagePath: String) -> Unit
    ) {

        val audioFile = Uri.fromFile(File(audioPath))
        FirebaseStorage.getInstance()
            .getReference(
                idChat +
                        "/Media/Recording/" +
                        userId +
                        "/" +
                        System.currentTimeMillis()
            )
            .putFile(audioFile)
            .addOnSuccessListener { success: UploadTask.TaskSnapshot ->
                val audioUrl =
                    success.storage.downloadUrl
                audioUrl.addOnCompleteListener { path: Task<Uri> ->
                    if (path.isSuccessful) {
                        onSuccess(path.result.toString())
                    }
                }
            }
    }


    fun uploadImage(
        selectedImageBytes: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        val ref = FirebaseStorage.getInstance().reference
            .child(FirebaseAuth.getInstance().currentUser?.uid!!)
            .child(Calendar.getInstance().time.toString())
        ref.putBytes(selectedImageBytes)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    onSuccess(it.toString())
                }
            }.addOnFailureListener {

            }
    }


    fun uploadVideo(
        uri: Uri,type: String,
        onSuccess: (videoPath: String) -> Unit, onFailure: (expception: Exception) -> Unit
    ) {

        val firebaseStorage = FirebaseStorage.getInstance()
        val videoReference = firebaseStorage.reference.child("video")
        val uploadTask: UploadTask = videoReference
            .child(uri.lastPathSegment + ".$type")
            .putFile(uri)

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener { exception ->
            onFailure(exception)
        }.addOnSuccessListener { success: UploadTask.TaskSnapshot ->
            val audioUrl =
                success.storage.downloadUrl
            audioUrl.addOnCompleteListener { path: Task<Uri> ->
                if (path.isSuccessful) {
                    onSuccess(path.result.toString())
                }
            }
        }
    }


}