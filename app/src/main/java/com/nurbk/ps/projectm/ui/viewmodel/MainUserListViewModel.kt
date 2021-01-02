package com.nurbk.ps.projectm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nurbk.ps.projectm.repository.MainUserListRepository
import com.nurbk.ps.projectm.repository.SignInRepository

class MainUserListViewModel(application: Application) : AndroidViewModel(application) {
    private val mainUserRepository = MainUserListRepository(application.applicationContext)


}