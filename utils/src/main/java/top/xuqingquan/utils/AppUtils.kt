@file:JvmName("AppUtils")

package top.xuqingquan.utils

import android.content.Context
import android.os.Build

/**
 * Create by 许清泉 on 2020/6/23 22:54
 */

/**
 * 获取版本号
 *
 * @param context
 * @return
 */
fun getVersionCode(context: Context): Long {
    return try {
        val packageInfo = context.packageManager
            .getPackageInfo(
                context.packageName,
                0
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    } catch (ex: Throwable) {
        Timber.w("getVersionCode error")
        try {
            val versionCode =
                ReflectUtils.reflect(context.packageName + ".BuildConfig").field("VERSION_CODE")
                    .get<Int>()
            versionCode.toLong()
        } catch (t: Throwable) {
            Timber.e(t, "getVersionCode error")
            0
        }
    }
}

/**
 * 获取版本名称
 *
 * @param context
 * @return
 */
fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager
            .getPackageInfo(
                context.packageName,
                0
            )

        packageInfo.versionName
    } catch (ex: Throwable) {
        Timber.w("getVersionName error")
        try {
            ReflectUtils.reflect(context.packageName + ".BuildConfig").field("VERSION_NAME").get()
        } catch (t: Throwable) {
            Timber.e(t, "getVersionName error")
            "unknown version"
        }
    }
}


//获取应用的名称
fun getApplicationName(context: Context): String? {
    return try {
        val packageManager = context.applicationContext.packageManager
        val applicationInfo = packageManager!!.getApplicationInfo(context.packageName, 0)
        packageManager.getApplicationLabel(applicationInfo).toString()
    } catch (e: Throwable) {
        Timber.w("getApplicationName error")
        try {
            val stringRes =
                context.resources.getIdentifier("app_name", "string", context.packageName)
            context.getString(stringRes)
        } catch (e: Throwable) {
            Timber.e("getApplicationName error")
            ""
        }
    }
}