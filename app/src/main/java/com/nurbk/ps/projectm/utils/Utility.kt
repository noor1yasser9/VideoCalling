package com.nurbk.ps.projectm.utils

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.nurbk.ps.projectm.utils.FileHelper
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utility {

    fun getTimeStamp(): Long {
        return System.currentTimeMillis()
    }

    fun setAudioTimeMmSs(textView: TextView, duration: Long) {
        val df = SimpleDateFormat("mm:ss", Locale.getDefault())
        textView.text = df.format(Date(duration))
    }

    fun getCameraIntent(context: Context, photoFile: File): Intent? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(context.packageManager) == null) return null
        val imageUri: Uri = FileProvider.getUriForFile(context, FileHelper.AUTHORITY, photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        return takePictureIntent
    }

        fun makeText(activity: AppCompatActivity, stringId: Int) {
            Toast.makeText(activity, activity.resources.getString(stringId), Toast.LENGTH_LONG).show()
        }

    fun makeText(activity: AppCompatActivity, string: String) {
        Toast.makeText(activity, string, Toast.LENGTH_LONG).show()
    }



    fun getMediaPath(context: Context, uri: Uri): String {

        val resolver = context.contentResolver
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = resolver.query(uri, projection, null, null, null)
            return if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                cursor.moveToFirst()
                cursor.getString(columnIndex)

            } else ""

        } catch (e: Exception) {
            resolver.let {
                val filePath = (context.applicationInfo.dataDir + File.separator
                        + System.currentTimeMillis())
                val file = File(filePath)

                resolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        val buf = ByteArray(4096)
                        var len: Int
                        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(
                            buf,
                            0,
                            len
                        )
                    }
                }
                return file.absolutePath
            }
        } finally {
            cursor?.close()
        }
    }

}