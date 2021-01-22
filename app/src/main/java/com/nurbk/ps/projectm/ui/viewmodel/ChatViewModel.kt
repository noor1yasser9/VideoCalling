package com.nurbk.ps.projectm.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.repository.ChatRepository
import com.nurbk.ps.projectm.repository.MainUserListRepository
import java.lang.Exception

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val chatRepository = ChatRepository(application.applicationContext)
    private val mainUserRepository = MainUserListRepository(application.applicationContext)

    fun sendMessage(
        channelId: String,
        message: Message,
    ) = chatRepository.sentMessage(channelId, message)


    fun createChatChannel(
        uid: String,
        onComplete: (channelId: String) -> Unit
    ) = chatRepository.createChatChannel(uid, onComplete)


    fun getAllMessage(channelId: String) =
        chatRepository.getAllMessage(channelId)

    fun getMessageLiveData() = chatRepository.getChat

    fun uploadUri(
        idChat: String,
        userId: String,
        audioPath: String,
        onSuccess: (imagePath: String) -> Unit
    ) = chatRepository.uploadUri(idChat, userId, audioPath, onSuccess)


    fun uploadImage(
        selectedImageBytes: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) = chatRepository.uploadImage(selectedImageBytes, onSuccess)

    fun uploadVideo(
        uri: Uri, type: String,
        onSuccess: (videoPath: String) -> Unit, onFailure: (expception: Exception) -> Unit
    ) = chatRepository.uploadVideo(uri, type, onSuccess, onFailure)


    fun updateData(
        data: Map<String, Any>,
        id: String,
        onComplete: () -> Unit
    ) =
        mainUserRepository.updateData(data, id, onComplete)

    fun getTyping(id: String, idRes: String) = chatRepository.getTyping(id, idRes)

    fun updateCollection(
        data: Map<String, Any>,
        id: String, idRes: String
    ) = chatRepository.updateCollection(data, id, idRes)

    val typing = chatRepository.typingLiveData

}