package com.nurbk.ps.projectm

import android.app.Activity
import android.app.Dialog

lateinit var dialog: Dialog
fun showDialog(activity: Activity) {
    dialog = Dialog(activity).apply {
        setContentView(R.layout.dialog_loading)
        setCancelable(false)
    }
    dialog.show()
}