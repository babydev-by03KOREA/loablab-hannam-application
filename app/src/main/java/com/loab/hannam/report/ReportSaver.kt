package com.loab.hannam.report

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore

/**
 * @desc
 * MediaStore에 저장 (Pictures/LoabHannam)
 * 생성된 Bitmap을 MediaStore를 통해 기기 갤러리에 PNG로 저장하는 기능
 * */
object ReportSaver {
    fun savePng(context: Context, bmp: Bitmap, fileName: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/LoabHannam"
            )
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { os ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, os)
            }
        }
        return uri
    }
}