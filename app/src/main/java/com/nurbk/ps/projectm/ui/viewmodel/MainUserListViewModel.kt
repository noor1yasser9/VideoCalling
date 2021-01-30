package com.nurbk.ps.projectm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.repository.MainUserListRepository
import com.nurbk.ps.projectm.repository.SignInRepository

class MainUserListViewModel(application: Application) : AndroidViewModel(application) {


    private val mainUserRepository = MainUserListRepository(application.applicationContext)

    val getAllUserLiveData = mainUserRepository.getAllUserLiveData
    val getAllGroupLiveData = mainUserRepository.getAllGroupLiveData

    fun updateUser(
        map: Map<String, Any>,
        id: String,
        collectionName: String,
        onComplete: () -> Unit
    ) =
        mainUserRepository.updateData(map, id, onComplete)

    fun getUpdate() = mainUserRepository.getUpdateLiveData()

    fun getToken(onComplete: () -> Unit) = mainUserRepository.getTokenId(onComplete)

    fun getLogOut() = mainUserRepository.logOut()

    fun getProfile(onComplete: () -> Unit) =
        mainUserRepository.signInRepository.getProfileData(
            FirebaseAuth.getInstance().currentUser!!.uid,
            onComplete
        )

    fun addGroup(
        currentUser: String,
        groupChannel: User
    ) = mainUserRepository.addGroup(currentUser, groupChannel)


    init {
        mainUserRepository.getAllUser()
        mainUserRepository.getAllGroup()
    }
}