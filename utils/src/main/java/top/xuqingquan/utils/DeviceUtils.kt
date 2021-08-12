@file:JvmName("DeviceUtils")

package top.xuqingquan.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.DisplayMetrics

/**
 * @author 许清泉 on 2019-04-29 23:28
 */

//获取屏幕相关参数
fun getDisplayMetrics(context: Context): DisplayMetrics {
    return context.resources.displayMetrics
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
@Suppress("DEPRECATION")
@SuppressLint("HardwareIds")
@RequiresPermission(Manifest.permission.READ_PHONE_STATE)
fun getIMEI(context: Context): String? {
    return try {
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
    } catch (t: Throwable) {
        null
    }
}

fun getAndroidID(context: Context): String {
    return Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: ""
}
