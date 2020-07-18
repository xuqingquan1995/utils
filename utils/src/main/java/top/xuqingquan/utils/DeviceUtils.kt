@file:JvmName("DeviceUtils")

package top.xuqingquan.utils

import android.Manifest
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresPermission
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * Created by 许清泉 on 2019-04-29 23:28
 */

//获取屏幕相关参数
fun getDisplayMetrics(context: Context): DisplayMetrics {
    val displaymetrics = DisplayMetrics()
    ContextCompat.getSystemService(context, WindowManager::class.java)
        ?.defaultDisplay?.getMetrics(displaymetrics)
    return displaymetrics
}

/**
 * 屏幕高度
 *
 * @param context
 * @return
 */
fun getScreenHeight(context: Context): Int {
    return getDisplayMetrics(context).heightPixels
}

/**
 * 屏幕宽度
 *
 * @param context
 * @return
 */
fun getScreenWidth(context: Context): Int {
    return getDisplayMetrics(context).widthPixels
}

/**
 * 获取IMEI
 */
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun getIMEI(context: Context): String? {
    return if (hasPermission(context, Manifest.permission.READ_PHONE_STATE)) {
        val tel = ContextCompat.getSystemService(context, TelephonyManager::class.java)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tel?.imei
        } else {
            tel?.deviceId
        }
    } else {
        null
    }
}

fun getAndroidID(context: Context): String {
    return Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}
