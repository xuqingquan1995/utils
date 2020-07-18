@file:JvmName("NetUtils")

package top.xuqingquan.utils

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.support.v4.content.ContextCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 * Created by 许清泉 on 2019-05-27 00:54
 */
@Suppress("DEPRECATION")
fun checkNetworkType(ctx: Context): Int {
    val context = ctx.applicationContext
    val netType = 0
    //连接管理对象
    val manager =
        ContextCompat.getSystemService(context, ConnectivityManager::class.java) ?: return netType
    //获取NetworkInfo对象
    val networkInfo = manager.activeNetworkInfo ?: return netType
    return when (networkInfo.type) {
        ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX, ConnectivityManager.TYPE_ETHERNET -> 1
        ConnectivityManager.TYPE_MOBILE -> when (networkInfo.subtype) {
            TelephonyManager.NETWORK_TYPE_LTE  // 4G
                , TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_EHRPD -> 2
            TelephonyManager.NETWORK_TYPE_UMTS // 3G
                , TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B -> 3
            TelephonyManager.NETWORK_TYPE_GPRS // 2G
                , TelephonyManager.NETWORK_TYPE_EDGE -> 4
            else -> netType
        }
        else -> netType
    }
}

fun checkNetworkTypeStr(ctx: Context): String {
    return when (checkNetworkType(ctx)) {
        1 -> "WIFI"
        2 -> "4G"
        3 -> "3G"
        4 -> "2G"
        else -> "未知"
    }
}

@Suppress("DEPRECATION")
fun networkIsConnect(ctx: Context, callback: MutableLiveData<Boolean>? = null): Boolean {
    return try {
        val context = ctx.applicationContext
        val connectivity = ContextCompat.getSystemService(context, ConnectivityManager::class.java)
        val info = connectivity?.activeNetworkInfo
        if (callback != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivity?.registerDefaultNetworkCallback(
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            super.onAvailable(network)
                            callback.postValue(true)
                        }

                        override fun onLost(network: Network) {
                            super.onLost(network)
                            callback.postValue(false)
                        }
                    })
            } else {
                callback.postValue(info != null && info.isConnected)
            }
        }
        info != null && info.isConnected
    } catch (e: Throwable) {
        false
    }
}

/**
 * 获取当前ip地址
 */
fun getIPAddress(useIPv4: Boolean): String {
    try {
        val nis: Enumeration<NetworkInterface?> = NetworkInterface.getNetworkInterfaces()
        val adds: LinkedList<InetAddress?> = LinkedList()
        while (nis.hasMoreElements()) {
            val ni = nis.nextElement() ?: continue
            // To prevent phone of xiaomi return "10.0.2.15"
            if (!ni.isUp || ni.isLoopback) continue
            val addresses = ni.inetAddresses
            while (addresses.hasMoreElements()) {
                adds.addFirst(addresses.nextElement())
            }
        }
        for (add in adds) {
            if (add == null) {
                continue
            }
            if (!add.isLoopbackAddress) {
                val hostAddress: String = add.hostAddress
                val isIPv4 = hostAddress.indexOf(':') < 0
                if (useIPv4) {
                    if (isIPv4) return hostAddress
                } else {
                    if (!isIPv4) {
                        val index = hostAddress.indexOf('%')
                        return if (index < 0) {
                            hostAddress.toUpperCase(Locale.getDefault())
                        } else {
                            hostAddress.substring(0, index).toUpperCase(Locale.getDefault())
                        }
                    }
                }
            }
        }
    } catch (e: SocketException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * 获取MAC地址
 *
 * @param context
 * @return
 */
fun getMacAddress(context: Context): String {

    var mac = "02:00:00:00:00:00"

    /**
     * Android  6.0 之前（不包括6.0）
     * @param context
     * @return
     */
    fun getMacDefault(context: Context): String {
        val wifi = ContextCompat.getSystemService(context, WifiManager::class.java) ?: return mac
        var info: WifiInfo? = null
        try {
            info = wifi.connectionInfo
        } catch (e: Exception) {
        }
        if (info == null) {
            return mac
        }
        @SuppressLint("HardwareIds")
        mac = info.macAddress
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.getDefault())
        }
        return mac
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    fun getMacFromFile(): String {
        try {
            mac = BufferedReader(FileReader(File("/sys/class/net/wlan0/address"))).readLine()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mac
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     * @return
     */
    fun getMacFromHardware(): String {
        try {
            val all = NetworkInterface.getNetworkInterfaces()
            for (nif in all) {
                if (!nif.name.equals("wlan0", ignoreCase = true)) continue
                val macBytes = nif.hardwareAddress ?: return ""
                val res1 = StringBuilder()
                for (b in macBytes) {
                    res1.append(String.format("%02X:", b))
                }
                if (res1.isNotEmpty()) {
                    res1.deleteCharAt(res1.length - 1)
                }
                return res1.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mac
    }

    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> mac = getMacDefault(context)
        Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> mac = getMacFromFile()
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> mac = getMacFromHardware()
    }
    return mac
}

fun getSSID(context: Context): String {
    val manager = ContextCompat.getSystemService(context, WifiManager::class.java)
    val info = manager?.connectionInfo
    return info?.ssid?.replace("\"", "") ?: "<unknown ssid>"
}

fun hasSim(context: Context): Boolean {
    val manager = ContextCompat.getSystemService(context, TelephonyManager::class.java)
    return manager?.simState == TelephonyManager.SIM_STATE_READY
}

fun getCellularOperatorType(context: Context): String {
    var operatorType = "网络未知"
    if (!networkIsConnect(context)) {
        operatorType = "网络没有连接"
        return operatorType
    }
    val networkType = checkNetworkTypeStr(context)
    if (!hasSim(context)) {
        return "$networkType-没有sim卡"
    }
    val manager = ContextCompat.getSystemService(context, TelephonyManager::class.java)
        ?: return operatorType
    return "${manager.simOperatorName}-$networkType"
}