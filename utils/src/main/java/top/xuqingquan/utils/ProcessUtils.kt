@file:JvmName("ProcessUtils")
package top.xuqingquan.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.Application
import android.content.Context
import android.os.Process
import android.text.TextUtils
import android.support.v4.content.ContextCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Adapted from com.blankj.utilcode.util.ProcessUtils#getCurrentProcessName
 */
fun getCurrentProcessName(context: Context): String {
    var name = getCurrentProcessNameByFile()
    if (!TextUtils.isEmpty(name)) return name
    name = getCurrentProcessNameByAms(context)
    if (!TextUtils.isEmpty(name)) return name
    name = getCurrentProcessNameByReflect(context)
    return name
}

private fun getCurrentProcessNameByFile(): String {
    return try {
        val file =
            File("/proc/" + Process.myPid().toString() + "/" + "cmdline")
        val mBufferedReader =
            BufferedReader(FileReader(file))
        val processName = mBufferedReader.readLine().trim { it <= ' ' }
        mBufferedReader.close()
        processName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

private fun getCurrentProcessNameByAms(context: Context): String {
    val am = ContextCompat.getSystemService(context, ActivityManager::class.java)
    val info: List<RunningAppProcessInfo>? = am?.runningAppProcesses
    if (info.isNullOrEmpty()) return ""
    val pid = Process.myPid()
    for (aInfo in info) {
        if (aInfo.pid == pid) {
            if (aInfo.processName != null) {
                return aInfo.processName
            }
        }
    }
    return ""
}

private fun getCurrentProcessNameByReflect(context: Context): String {
    val processName = try {
        val app = context.applicationContext as Application
        val loadedApkField = app.javaClass.getField("mLoadedApk")
        loadedApkField.isAccessible = true
        val loadedApk = loadedApkField.get(app)
        val activityThreadField = loadedApk?.javaClass?.getDeclaredField("mActivityThread")
        activityThreadField?.isAccessible = true
        val activityThread = activityThreadField?.get(loadedApk)
        val getProcessName =
            activityThread?.javaClass?.getDeclaredMethod("getProcessName")
        getProcessName?.invoke(activityThread)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    return processName?.toString() ?: ""
}