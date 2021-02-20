package com.nurbk.ps.projectm.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.repository.SignInRepository
import com.nurbk.ps.projectm.repository.SignUpRepository

class SignInAuthViewModel(application: Application) : AndroidViewModel(application) {


    private val sigInRepository = SignInRepository(application.applicationContext)
    private val signUpRepository = SignUpRepository(application.applicationContext)


    fun signInWithEmailAndPassword(email: String, password: String) =
        sigInRepository.signIn(email = email, password = password)


    fun getSignIn() = sigInRepository.getSignIn()

    fun insertUsers(users: User) {
        signUpRepository.insertUser(users)
        sigInRepository.getProfileData(users.id) {
        }
    }
}