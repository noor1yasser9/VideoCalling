package com.nurbk.ps.projectm.others

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nurbk.ps.projectm.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.ArrayList


fun permission(context: Context, permission: ArrayList<String>, onComplete: () -> Unit) {
    Dexter.withContext(context)
        .withPermissions(
            permission
        )
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if (report.areAllPermissionsGranted()) {
                        onComplete()
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {

                token?.continuePermissionRequest()
            }
        })
        .withErrorListener {

        }
        .check()
}


fun String.saveTo(path: String) {
    val file = File(path)
    if (file.exists()) return
    URL(this).openStream().use { input ->
        try {
            if (file.parentFile?.exists() != true) file.parentFile?.mkdirs()
            file.createNewFile()
            FileOutputStream(file).use { output -> input.copyTo(output) }
        } catch (e: Exception) {
            Log.e("TAG", e.toString())
            throw e
        }
    }
}



fun compressFormat(data: Uri, activity: Activity): ByteArray {
    val selectImageBmp = MediaStore
        .Images.Media.getBitmap(
            activity.contentResolver, data
        )
    val outputStream = ByteArrayOutputStream()
    selectImageBmp
        .compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
    return outputStream.toByteArray()
}
