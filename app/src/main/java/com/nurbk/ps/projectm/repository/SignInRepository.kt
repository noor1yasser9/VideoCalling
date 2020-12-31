package com.nurbk.ps.projectm.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth

class SignInRepository private constructor(context: Context) {

    private val sigInLiveData = MutableLiveData<Boolean>()

    companion object {
        @Volatile
        private var instance: SignInRepository? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                instance ?: createRepository(context).also {
                    instance = it
                }
            }

        private fun createRepository(context: Context) =
            SignInRepository(context)

    }

    fun signIn(email: String, password: String) =
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    sigInLiveData.postValue(true)
                } else {
                    sigInLiveData.postValue(false)
                }
            }

    fun getSignIn(): LiveData<Boolean> = sigInLiveData
}