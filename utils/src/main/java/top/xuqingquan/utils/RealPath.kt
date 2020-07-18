@file:JvmName("RealPath")

package top.xuqingquan.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


/**
 * Created by 许清泉 on 2018/02/12 19:10
 */
fun getPath(context: Context, uri: Uri): String? {
    if (DocumentsContract.isDocumentUri(context, uri)) {
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                @Suppress("DEPRECATION")
                return "${Environment.getExternalStorageDirectory()}/${split[1]}"
            }
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                id.toLong()
            )
            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            when (type) {
                "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "${MediaStore.Images.Media._ID}=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri!!, selection, selectionArgs)
        }
    } else if (uri.authority == "${context.packageName}.ScaffoldFileProvider") {
        return "${context.getExternalFilesDir("")}/${uri.path}"
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {
        return if (isGooglePhotosUri(uri)) {
            uri.lastPathSegment
        } else {
            getDataColumn(context, uri, null, null)
        }
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }
    return null
}

private fun getDataColumn(
    context: Context,
    uri: Uri,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    @Suppress("DEPRECATION") val column = MediaStore.Images.Media.DATA
    val projection = arrayOf(column)
    val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
    if (cursor != null && cursor.moveToFirst()) {
        val index = cursor.getColumnIndexOrThrow(column)
        val result = cursor.getString(index)
        cursor.close()
        return result
    }
    return null
}

private fun isExternalStorageDocument(uri: Uri): Boolean =
    ("com.android.externalstorage.documents" == uri.authority)

private fun isDownloadsDocument(uri: Uri): Boolean =
    ("com.android.providers.downloads.documents" == uri.authority)

private fun isMediaDocument(uri: Uri): Boolean =
    ("com.android.providers.media.documents" == uri.authority)

private fun isGooglePhotosUri(uri: Uri): Boolean =
    ("com.google.android.apps.photos.content" == uri.authority)
