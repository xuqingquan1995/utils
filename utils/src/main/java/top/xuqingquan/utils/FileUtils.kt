@file:JvmName("FileUtils")
package top.xuqingquan.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StatFs
import android.text.format.DateUtils
import java.io.Closeable
import java.io.File
import java.util.*

/**
 * Created by 许清泉 on 2019/4/14 21:49
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
    val type: String
    val fName = f.name
    /* 取得扩展名 */
    val end = fName.substring(fName.lastIndexOf(".") + 1).toLowerCase(Locale.getDefault())
    /* 依扩展名的类型决定MimeType */
    type = when (end) {
        "pdf" -> "application/pdf"//
        "m4a", "mp3", "mid", "xmf", "ogg", "wav" -> "audio/*"
        "3gp", "mp4" -> "video/*"
        "jpg", "gif", "png", "jpeg", "bmp" -> "image/*"
        "apk" -> "application/vnd.android.package-archive"
        "pptx", "ppt" -> "application/vnd.ms-powerpoint"
        "docx", "doc" -> "application/vnd.ms-word"
        "xlsx", "xls" -> "application/vnd.ms-excel"
        else -> "*/*"
    }
    return type
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
 * 将一系列uri转为String的路径
 */
fun uriToPath(context: Context?, uris: Array<Uri>?): Array<String?>? {
    if (context == null || uris.isNullOrEmpty()) {
        return null
    }
    try {
        val paths = arrayOfNulls<String>(uris.size)
        var i = 0
        for (mUri in uris) {
            paths[i++] = getPath(context.applicationContext, mUri)
        }
        return paths
    } catch (throwable: Throwable) {
        Timber.e(throwable)
    }

    return null
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

private val units = arrayOf("B", "KB", "MB", "GB", "TB")
/**
 * 单位转换
 */
fun getSizeUnit(size: Double): String {
    var sizeUnit = size
    var index = 0
    while (sizeUnit > 1024 && index < 4) {
        sizeUnit /= 1024.0
        index++
    }
    return String.format(Locale.getDefault(), "%.2f %s", sizeUnit, units[index])
}