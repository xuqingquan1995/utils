@file:JvmName("FileUtils")

package top.xuqingquan.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.text.format.DateUtils
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * @author 许清泉 on 2019/4/14 21:49
 */
/**
 * 创建未存在的文件夹
 *
 * @param file
 * @return
 */
fun makeDirs(file: File): File {
    if (!file.exists()) {
        file.mkdirs()
    }
    return file
}

/**
 * 返回缓存文件夹
 */
fun getCacheFile(context: Context): File {
    return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
        var file: File?
        file = context.externalCacheDir//获取系统管理的sd卡缓存文件
        if (file == null) {//如果获取的文件为空,就使用自己定义的缓存文件夹做缓存路径
            file = File(getCacheFilePath(context))
            makeDirs(file)
        }
        file
    } else {
        context.cacheDir
    }
}

/**
 * 获取自定义缓存文件地址
 *
 * @param context
 * @return
 */
fun getCacheFilePath(context: Context): String {
    return context.getExternalFilesDir("")?.path ?: ""
}

/**
 * 获得文件mime
 */
fun getMIMEType(f: File): String {
    return when (f.extension) {
        "pdf" -> "application/pdf"
        "apk" -> "application/vnd.android.package-archive"
        "bmp", "gif", "jpeg", "jpg", "png" -> "image/*"
        "3gp", "asf", "avi", "m4u", "m4v", "mov", "mp4", "mpe", "mpeg", "mpg", "mpg4" -> "video/*"
        "m3u", "m4a", "m4b", "m4p", "mp2", "mp3", "mpga", "ogg", "rmvb", "wav", "wma", "wmv" -> "audio/*"
        "c", "conf", "cpp", "h", "java", "log", "prop", "rc", "sh", "txt", "text", "xml" -> "text/plain"
        "htm", "html" -> "text/html"
        "pps", "ppt", "pptx" -> "application/vnd.ms-powerpoint"
        "doc", "docx" -> "application/msword"
        "xls", "xlsx" -> "application/vnd.ms-excel"
        "tar" -> "application/x-tar"
        "rar" -> "application/vnd.rar"
        "tgz" -> "application/x-compressed"
        "z" -> "application/x-compress"
        "zip" -> "application/zip"
        "7z" -> "application/x-7z-compressed"
        else -> "*/*"
    }
}

/**
 * 删除多久没有修改过的文件
 */
fun clearCacheFolder(dir: File?, numDays: Int): Int {
    var deletedFiles = 0
    if (dir != null) {
        Timber.i("dir:" + dir.absolutePath)
    }
    if (dir != null && dir.isDirectory) {
        try {
            for (child in dir.listFiles() ?: arrayOf<File>()) {
                //first delete subdirectories recursively
                if (child.isDirectory) {
                    deletedFiles += clearCacheFolder(child, numDays)
                }
                //then delete the files and subdirectories in this dir
                //only empty directories can be deleted, so subdirs have been done first
                if (child.lastModified() < Date().time - numDays * DateUtils.DAY_IN_MILLIS) {
                    Timber.i("file name:" + child.name)
                    if (child.delete()) {
                        deletedFiles++
                    }
                }
            }
        } catch (e: Throwable) {
            Timber.e(e, "Failed to clean the cache, result %s", e.message)
        }

    }
    return deletedFiles
}

/**
 * 获取文件uri
 */
fun getUriFromFile(context: Context, file: File): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        ScaffoldFileProvider.getUriForFile(
            context, "${context.packageName}.ScaffoldFileProvider", file
        )
    } else {
        Uri.fromFile(file)
    }
}

/**
 * 将一系列uri转为String的路径
 */
fun uriToPath(context: Context?, uris: Array<Uri>?): Array<String?>? {
    if (context == null || uris.isNullOrEmpty()) {
        return null
    }
    try {
        val paths = arrayOfNulls<String>(uris.size)
        uris.forEachIndexed { index, uri ->
            paths[index] = uriToPath(context,uri)
        }
        return paths
    } catch (throwable: Throwable) {
        Timber.e(throwable)
    }

    return null
}

/**
 * 单个uri转文件路径
 */
fun uriToPath(context: Context?, uri: Uri?): String? {
    if (context == null || uri == null) {
        return null
    }
    try {
        val aboveN = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        return if (aboveN) {
            getFilePathFromUri(context.applicationContext, uri)
        } else {
            getPath(context.applicationContext, uri)
        }
    } catch (t: Throwable) {
        Timber.e(t)
    }
    return null
}

/**
 * 将file转为Media类型的uri
 * 在Android 10 上可以解决无法从file上拿到真实文件的问题
 * 只拿Uri去处理
 */
fun getFileMediaUrl(context: Context, uri: Uri): Uri? {
    if (uri.scheme != "file") {
        return uri
    }
    val cursor = context.contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        arrayOf(MediaStore.Images.Media._ID),
        MediaStore.Images.Media.DATA + "=? ",
        arrayOf(uri.path),
        null
    )
    if (cursor == null) {
        Timber.d("cursor==null")
        return null
    }
    if (cursor.moveToFirst()) {
        val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
        val baseUri = Uri.parse("content://media/external/images/media")
        val uri2 = Uri.withAppendedPath(baseUri, "" + id)
        Timber.d("uri2=>${uri2}")
        return uri2
    } else {
        Timber.d("cursor.moveToFirst()")
    }
    cursor.close()
    return null
}

fun getFileNameFromUri(uri: Uri): String? {
    val path = uri.path ?: return null
    val cut = path.lastIndexOf("/")
    if (cut != -1) {
        return path.substring(cut + 1)
    }
    return null
}

fun copyFileFromUri(context: Context, srcUri: Uri, dstFile: File) {
    //val pfd = context.contentResolver.openFileDescriptor(srcUri, "r") ?: return
    //val inputStream = FileInputStream(pfd.fileDescriptor)
    //部分Android 7.0 设备使用openFileDescriptor 读取文件会出错，openInputStream正常
    val inputStream = context.contentResolver.openInputStream(srcUri) ?: return
    val outputStream = FileOutputStream(dstFile)
    val bufferSize = 1024 * 2
    val buffer = ByteArray(bufferSize)
    try {
        var n: Int
        while (inputStream.read(buffer, 0, bufferSize).also { n = it } != -1) {
            outputStream.write(buffer, 0, n)
        }
        outputStream.flush()
    } finally {
        closeIO(outputStream)
        closeIO(inputStream)
    }
}

fun getFilePathFromUri(context: Context, uri: Uri): String? {
    val rootDir = context.cacheDir
    val fileName = getFileNameFromUri(uri) ?: return null
    val copyFile = File(rootDir, fileName)
    copyFileFromUri(context, uri, copyFile)
    return copyFile.absolutePath
}

/**
 * 关闭IO流
 */
fun closeIO(closeable: Closeable?) {
    try {
        closeable?.close()
    } catch (e: Throwable) {
        e.printStackTrace()
    }

}

/**
 * 获取可用空间
 */
fun getAvailableStorage(context: Context): Long {
    return try {
        StatFs(context.cacheDir.path).availableBytes
    } catch (ex: RuntimeException) {
        0
    }
}

/**
 * 获取总存储空间
 */
fun getTotalStorage(context: Context): Long {
    return try {
        StatFs(context.cacheDir.path).totalBytes
    } catch (ex: RuntimeException) {
        0
    }
}

private val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB", "BB", "NB", "DB")

/**
 * 单位转换
 */
fun getSizeUnit(size: Double): String {
    var sizeUnit = size
    var index = 0
    while (sizeUnit >= 1024 && index < units.size) {
        sizeUnit /= 1024.0
        index++
    }
    return String.format(Locale.getDefault(), "%.2f %s", sizeUnit, units[index])
}