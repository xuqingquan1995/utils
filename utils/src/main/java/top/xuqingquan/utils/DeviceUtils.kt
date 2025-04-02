@file:JvmName("DeviceUtils")

package top.xuqingquan.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat

/**
 * @author 许清泉 on 2019-04-29 23:28
 */
fun getScreenSize(context: Context): Point {
    val windowManager =
        ContextCompat.getSystemService(context, WindowManager::class.java) ?: return Point(0, 0)
    return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        val metrics = windowManager.currentWindowMetrics
        val bounds = metrics.bounds
        Point(bounds.width(), bounds.height())
    } else {
        val display = windowManager.defaultDisplay
        val out = Point()
        display.getRealSize(out)
        out
    }
}

/**
 * 屏幕高度
 *
 * @param context
 * @return
 */
fun getScreenHeight(context: Context): Int {
    return getScreenSize(context).y
}

/**
 * 屏幕宽度
 *
 * @param context
 * @return
 */
fun getScreenWidth(context: Context): Int {
    return getScreenSize(context).x
}

/**
 * 获取IMEI
 */
var imeiCache: String? = null

@Suppress("DEPRECATION")
@SuppressLint("HardwareIds")
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun getIMEI(context: Context, readCache: Boolean = true): String? {
    if (readCache && imeiCache != null) {
        return imeiCache
    }
    imeiCache = try {
        if (hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
            val tel = ContextCompat.getSystemService(context, TelephonyManager::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tel?.imei
            } else {
                tel?.deviceId
            }
        } else {
            null
        }
    } catch (_: Throwable) {
        null
    }
    return imeiCache
}

/**
 * 不同的设备可能会产生相同的ANDROID_ID：9774d56d682e549c；有些设备返回的值为null;
 */
var androidIDCache = ""
fun getAndroidID(context: Context, readCache: Boolean = true): String {
    if (readCache && androidIDCache.isNotEmpty()) {
        return androidIDCache
    }
    var id = Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    if (id == null || id == "9774d56d682e549c") {
        id = ""
    }
    androidIDCache = id
    return androidIDCache
}
