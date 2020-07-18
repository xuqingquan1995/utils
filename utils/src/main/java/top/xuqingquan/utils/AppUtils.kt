@file:JvmName("AppUtils")

package top.xuqingquan.utils

import android.content.Context
import android.content.pm.PackageManager
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
        0
    }
}


//获取应用的名称
fun getApplicationName(context: Context): String? {
    return try {
        val packageManager = context.applicationContext.packageManager
        val applicationInfo = packageManager!!.getApplicationInfo(context.packageName, 0)
        packageManager.getApplicationLabel(applicationInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
}