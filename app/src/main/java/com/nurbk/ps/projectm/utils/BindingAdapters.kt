package com.nurbk.ps.projectm.utils

import android.util.Log
import android.view.View
import androidx.databinding.BindingAdapter
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.others.saveTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@BindingAdapter("fakeAudioProgress")
fun bindingFakeAudioProgress(view: View, msg: Message?) {
    if (msg == null) return
    CoroutineScope(Dispatchers.Main).launch {
        try {
            view.visibility = View.VISIBLE
            getAudio(msg)
            msg.audioDownloaded = true
            view.visibility = View.GONE
        } catch (e: Exception) {
            view.visibility = View.GONE
        }
    }
}


suspend fun getAudio(msg: Message) {
    withContext(Dispatchers.IO) { msg.audioFile.let { msg.audioUrl.saveTo(it) } }
}

