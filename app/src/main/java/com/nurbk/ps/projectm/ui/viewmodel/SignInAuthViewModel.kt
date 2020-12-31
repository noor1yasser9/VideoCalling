package com.nurbk.ps.projectm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.repository.SignInRepository

class SignInAuthViewModel(application: Application) : AndroidViewModel(application) {


    private val sigInRepository = SignInRepository(application.applicationContext)


    fun signInWithEmailAndPassword(email: String, password: String) =
        sigInRepository.signIn(email = email, password = password)

    fun getSignIn() = sigInRepository.getSignIn()


}