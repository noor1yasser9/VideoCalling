package com.nurbk.ps.projectm.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nurbk.ps.projectm.others.COLLECTION_USERS

class MainUserListRepository private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: MainUserListRepository? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                instance ?: createRepository(context).also {
                    instance = it
                }
            }

        private fun createRepository(context: Context) =
            MainUserListRepository(context)
    }


    fun updateData(data: Map<String, Any>) = FirebaseFirestore
        .getInstance()
        .collection(FirebaseAuth.getInstance().uid.toString())
        .document()
        .update(
            data
        )

}