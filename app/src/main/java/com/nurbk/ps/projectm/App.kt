package com.nurbk.ps.projectm

import android.app.Application
import android.content.Context
import com.danikula.videocache.HttpProxyCacheServer


class App : Application() {
    private var proxy: HttpProxyCacheServer? = null
    private fun newProxy(): HttpProxyCacheServer {
        return HttpProxyCacheServer(this)
    }

    companion object {
        fun getProxy(context: Context): HttpProxyCacheServer {
            val app = context.applicationContext as App
            return if (app.proxy == null) app.newProxy().also { app.proxy = it } else app.proxy!!
        }
    }
}