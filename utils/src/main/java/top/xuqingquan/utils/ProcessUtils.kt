@file:JvmName("ProcessUtils")

package top.xuqingquan.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.AppOpsManager
import android.app.Application
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


/**
 * Adapted from com.blankj.utilcode.util.ProcessUtils#getCurrentProcessName
 */

/**
 * Return the foreground process name.
 *
 * Target APIs greater than 21 must hold
 * `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />`
 *
 * @return the foreground process name
 */
@SuppressLint("QueryPermissionsNeeded")
fun getForegroundProcessName(context: Context): String {
    val am = ContextCompat.getSystemService(context, ActivityManager::class.java)
    val pInfo = am?.runningAppProcesses
    if (pInfo != null && pInfo.size > 0) {
        for (aInfo in pInfo) {
            if (aInfo.importance
                == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            ) {
                return aInfo.processName
            }
        }
    }
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
        val pm = context.packageManager
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        val list = pm?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        if (list == null || list.size <= 0) {
            Timber.i("getForegroundProcessName: noun of access to usage information.")
            return ""
        }
        try { // Access to usage information.
            val info = pm.getApplicationInfo(context.packageName, 0)
            val aom = ContextCompat.getSystemService(context, AppOpsManager::class.java)
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    aom?.unsafeCheckOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        info.uid,
                        info.packageName
                    ) != AppOpsManager.MODE_ALLOWED
                } else {
                    aom?.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        info.uid,
                        info.packageName
                    ) != AppOpsManager.MODE_ALLOWED
                }
            ) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    aom?.unsafeCheckOpNoThrow(
                            AppOpsManager.OPSTR_GET_USAGE_STATS,
                            info.uid,
                            info.packageName
                        ) != AppOpsManager.MODE_ALLOWED
                } else {
                    aom?.checkOpNoThrow(
                        AppOpsManager.OPSTR_GET_USAGE_STATS,
                        info.uid,
                        info.packageName
                    ) != AppOpsManager.MODE_ALLOWED
                }
            ) {
                Timber.i("getForegroundProcessName: refuse to device usage stats.")
                return ""
            }
            val usageStatsManager =
                ContextCompat.getSystemService(context, UsageStatsManager::class.java)
            var usageStatsList: List<UsageStats>? = null
            if (usageStatsManager != null) {
                val endTime = System.currentTimeMillis()
                val beginTime = endTime - 86400000 * 7
                usageStatsList = usageStatsManager
                    .queryUsageStats(
                        UsageStatsManager.INTERVAL_BEST,
                        beginTime, endTime
                    )
            }
            if (usageStatsList == null || usageStatsList.isEmpty()) return ""
            var recentStats: UsageStats? = null
            for (usageStats in usageStatsList) {
                if (recentStats == null
                    || usageStats.lastTimeUsed > recentStats.lastTimeUsed
                ) {
                    recentStats = usageStats
                }
            }
            return recentStats?.packageName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
    return ""
}

/**
 * Return all background processes.
 *
 * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
 *
 * @return all background processes
 */
@RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)
fun getAllBackgroundProcesses(context: Context): Set<String?> {
    val am = ContextCompat.getSystemService(context, ActivityManager::class.java)
    val info = am?.runningAppProcesses
    val set = mutableSetOf<String>()
    info?.forEach {
        set.addAll(it.pkgList)
    }
    return set
}

/**
 * Kill all background processes.
 *
 * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
 *
 * @return background processes were killed
 */
@RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)
fun killAllBackgroundProcesses(context: Context): Set<String> {
    val am = ContextCompat.getSystemService(context, ActivityManager::class.java)
    var info = am?.runningAppProcesses
    val set = mutableSetOf<String>()
    if (info == null) {
        return set
    }
    info.forEach {
        it.pkgList.forEach { pkg ->
            am?.killBackgroundProcesses(pkg)
            set.add(pkg)
        }
    }
    info = am?.runningAppProcesses
    info?.forEach {
        it.pkgList.forEach { pkg ->
            set.remove(pkg)
        }
    }
    return set
}

/**
 * Kill background processes.
 *
 * Must hold `<uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES" />`
 *
 * @param packageName The name of the package.
 * @return `true`: success<br></br>`false`: fail
 */
@RequiresPermission(Manifest.permission.KILL_BACKGROUND_PROCESSES)
fun killBackgroundProcesses(context: Context, packageName: String): Boolean {
    val am = ContextCompat.getSystemService(context, ActivityManager::class.java)
    var info = am?.runningAppProcesses
    if (info == null || info.size == 0) {
        return true
    }
    info.forEach {
        if (packageName in it.pkgList) {
            am?.killBackgroundProcesses(packageName)
        }
    }
    info = am?.runningAppProcesses
    if (info == null || info.size == 0) {
        return true
    }
    for (aInfo in info) {
        if (packageName in aInfo.pkgList) {
            return false
        }
    }
    return true
}

/**
 * Return whether app running in the main process.
 *
 * @return `true`: yes<br></br>`false`: no
 */
fun isMainProcess(context: Context): Boolean {
    return context.packageName == getCurrentProcessName(context)
}

/**
 * Return the name of current process.
 *
 * @return the name of current process
 */
fun getCurrentProcessName(context: Context): String {
    var name = getCurrentProcessNameByFile()
    if (name.isNotEmpty()) {
        return name
    }
    name = getCurrentProcessNameByAms(context)
    if (name.isNotEmpty()) {
        return name
    }
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