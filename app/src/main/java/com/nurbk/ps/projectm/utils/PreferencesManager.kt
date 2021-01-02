package com.nurbk.ps.projectm.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.nurbk.ps.projectm.others.NAME_FILE_PREF

@SuppressLint("CommitPrefEdits")
class PreferencesManager private constructor(context: Context) {


    private var preferences: SharedPreferences =
        context.getSharedPreferences(NAME_FILE_PREF, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()


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


    fun getPreferences() = preferences
    fun getEditor() = editor


}