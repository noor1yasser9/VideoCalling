package com.nurbk.ps.projectm.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.others.COLLECTION_USERS
import com.nurbk.ps.projectm.others.IS_SIGN_IN
import com.nurbk.ps.projectm.others.USER_DATA_PROFILE
import com.nurbk.ps.projectm.utils.PreferencesManager

class SignInRepository private constructor(context: Context) {

    private val sigInLiveData = MutableLiveData<Boolean>()
    private val sharedPreferences = PreferencesManager(context)

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
                    getProfileData(FirebaseAuth.getInstance().uid.toString())
                    sigInLiveData.postValue(true)
                    sharedPreferences.getEditor().putBoolean(IS_SIGN_IN, true).apply()
                } else {
                    sigInLiveData.postValue(false)
                }
            }

    private fun getProfileData(uid: String) =
        FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
            .document(uid)
            .addSnapshotListener { querySnapshot, _ ->
                val userString = Gson().toJson(querySnapshot!!.toObject(User::class.java))
                sharedPreferences.getEditor().putString(USER_DATA_PROFILE, userString).apply()
            }

    fun getSignIn(): LiveData<Boolean> = sigInLiveData




}