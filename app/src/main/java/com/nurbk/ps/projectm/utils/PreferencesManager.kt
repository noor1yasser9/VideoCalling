package com.nurbk.ps.projectm.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.nurbk.ps.projectm.model.User
import com.nurbk.ps.projectm.others.NAME_FILE_PREF
import com.nurbk.ps.projectm.others.USER_DATA_PROFILE

@SuppressLint("CommitPrefEdits")
class PreferencesManager private constructor(context: Context) {


    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    companion object {
        @Volatile
        private var instance: PreferencesManager? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) =
            instance ?: synchronized(LOCK) {
                instance ?: createPreferences(context).also {
                    instance = it
                }
            }

        private fun createPreferences(context: Context) =
            PreferencesManager(context)

    }
    init {
        preferences = context.getSharedPreferences(NAME_FILE_PREF, Context.MODE_PRIVATE)
        editor = preferences!!.edit()
    }

    fun getPreferences() = preferences
    fun getEditor() = editor

    fun getUserProfile(): User =
        Gson().fromJson(preferences!!.getString(USER_DATA_PROFILE, ""), User::class.java)
}