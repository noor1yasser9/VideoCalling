package com.nurbk.ps.projectm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.nurbk.ps.projectm.repository.MainUserListRepository
import com.nurbk.ps.projectm.repository.SignInRepository

class MainUserListViewModel(application: Application) : AndroidViewModel(application) {


    private val mainUserRepository = MainUserListRepository(application.applicationContext)

    val getAllUserLiveData = mainUserRepository.getAllUserLiveData

    fun updateUser(
        map: Map<String, String>,
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


    init {
        mainUserRepository.getAllUser()
    }
}