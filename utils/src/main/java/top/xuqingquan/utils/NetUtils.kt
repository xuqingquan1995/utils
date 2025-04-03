@file:JvmName("NetUtils")

package top.xuqingquan.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*


/**
 * @author 许清泉 on 2019-05-27 00:54
 */
@Suppress("DEPRECATION")
fun checkNetworkType(ctx: Context): Int {
    return checkNetworkType(ctx, true)
}

var checkNetworkTypeCache = -1

fun checkNetworkType(ctx: Context, readCache: Boolean = true): Int {
    if (readCache && checkNetworkTypeCache != -1) {
        return checkNetworkTypeCache
    }
    val context = ctx.applicationContext
    val netType = 0
    //连接管理对象
    val manager =
        ContextCompat.getSystemService(context, ConnectivityManager::class.java) ?: return netType
    //获取NetworkInfo对象
    val networkInfo = manager.activeNetworkInfo ?: return netType
    checkNetworkTypeCache = when (networkInfo.type) {
        ConnectivityManager.TYPE_WIFI, ConnectivityManager.TYPE_WIMAX, ConnectivityManager.TYPE_ETHERNET -> 1
        ConnectivityManager.TYPE_MOBILE -> when (networkInfo.subtype) {
            TelephonyManager.NETWORK_TYPE_GPRS, // 2G
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
                -> 2

            TelephonyManager.NETWORK_TYPE_UMTS, // 3G
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_HSPA,
                -> 3

            TelephonyManager.NETWORK_TYPE_LTE,  // 4G
            19,// 19 对应的是 NETWORK_TYPE_LTE_CA，被标记为 hide 了，所以直接使用 19 判断
            TelephonyManager.NETWORK_TYPE_IWLAN,
                -> 4

            TelephonyManager.NETWORK_TYPE_NR -> 5
            else -> netType
        }

        else -> netType
    }
    return checkNetworkTypeCache
}

fun checkNetworkTypeStr(ctx: Context): String {
    return checkNetworkTypeStr(ctx, true)
}

var networkTypeStrCache = ""

fun checkNetworkTypeStr(ctx: Context, readCache: Boolean = true): String {
    if (readCache && networkTypeStrCache.isNotEmpty()) {
        return networkTypeStrCache
    }
    networkTypeStrCache = when (val type = checkNetworkType(ctx)) {
        1 -> "WIFI"
        2 -> "2G"
        3 -> "3G"
        4 -> "4G"
        5 -> "5G"
        else -> "未知-${type}"
    }
    return networkTypeStrCache
}

fun networkIsConnect(
    ctx: Context,
    callback: MutableLiveData<Boolean>? = null
): Boolean {
    return networkIsConnect(ctx, callback, true)
}

var networkIsConnectCache: Boolean? = null

@Suppress("DEPRECATION")
fun networkIsConnect(
    ctx: Context,
    callback: MutableLiveData<Boolean>? = null,
    readCache: Boolean = true
): Boolean {
    if (readCache && networkIsConnectCache != null) {
        return networkIsConnectCache!!
    }
    networkIsConnectCache = try {
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
    } catch (_: Throwable) {
        false
    }
    return networkIsConnectCache!!
}

/**
 * 获取当前ip地址
 */
fun getIPAddress(useIPv4: Boolean): String {
    return getIPAddress(useIPv4, true)
}

const val UNKNOWN_IP_ADDRESS = "0.0.0.0"
var ipAddressCache = UNKNOWN_IP_ADDRESS

fun getIPAddress(useIPv4: Boolean, readCache: Boolean = true): String {
    if (readCache && ipAddressCache != UNKNOWN_IP_ADDRESS) {
        return ipAddressCache
    }
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
                val hostAddress = add.hostAddress
                if (hostAddress == null) {
                    ipAddressCache = UNKNOWN_IP_ADDRESS
                    break
                }
                val isIPv4 = hostAddress.indexOf(':') < 0
                if (useIPv4) {
                    if (isIPv4) {
                        ipAddressCache = hostAddress
                        break
                    }
                } else {
                    if (!isIPv4) {
                        val index = hostAddress.indexOf('%')
                        ipAddressCache = if (index < 0) {
                            hostAddress.uppercase(Locale.getDefault())
                        } else {
                            hostAddress.substring(0, index).uppercase(Locale.getDefault())
                        }
                        break
                    }
                }
            }
        }
    } catch (e: SocketException) {
        e.printStackTrace()
    }
    return ipAddressCache
}

/**
 * 获取MAC地址
 *
 * @param context
 * @param readCache
 * @return
 */
fun getMacAddress(context: Context): String {
    return getMacAddress(context, true)
}

const val UNKNOWN_MAC_ADDRESS = "02:00:00:00:00:00"
var macAddressCache = UNKNOWN_MAC_ADDRESS
fun getMacAddress(context: Context, readCache: Boolean = true): String {

    if (readCache && macAddressCache != UNKNOWN_MAC_ADDRESS) {
        return macAddressCache
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * @param context
     * @return
     */
    fun getMacDefault(context: Context): String {
        val wifi = ContextCompat.getSystemService(context, WifiManager::class.java)
            ?: return UNKNOWN_MAC_ADDRESS
        var info: WifiInfo? = null
        try {
            info = wifi.connectionInfo
        } catch (_: Exception) {
        }
        if (info == null) {
            return UNKNOWN_MAC_ADDRESS
        }
        @SuppressLint("HardwareIds")
        var mac = info.macAddress
        if ((mac.isEmpty() || mac == UNKNOWN_MAC_ADDRESS) && hasPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            @SuppressLint("HardwareIds")
            mac = info.macAddress
            if (mac.isEmpty()) {
                return UNKNOWN_MAC_ADDRESS
            }
        }
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.uppercase(Locale.getDefault())
        }
        return mac
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    fun getMacFromFile(): String {
        return try {
            BufferedReader(FileReader(File("/sys/class/net/wlan0/address"))).readLine()
        } catch (_: Exception) {
            UNKNOWN_MAC_ADDRESS
        }
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
        return UNKNOWN_MAC_ADDRESS
    }

    macAddressCache = when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> getMacDefault(context)
        Build.VERSION.SDK_INT < Build.VERSION_CODES.N -> getMacFromFile()
        else -> getMacFromHardware()
    }
    return macAddressCache
}

fun getSSID(context: Context): String {
    return getSSID(context, true)
}

const val UNKNOWN_SSID = "<unknown ssid>"
var ssidCache = UNKNOWN_SSID
fun getSSID(context: Context, readCache: Boolean = true): String {
    if (readCache && ssidCache != UNKNOWN_SSID) {
        return ssidCache
    }
    val manager = ContextCompat.getSystemService(context, WifiManager::class.java)
    val info = manager?.connectionInfo
    ssidCache = info?.ssid?.replace("\"", "") ?: UNKNOWN_SSID
    return ssidCache
}

fun hasSim(context: Context): Boolean {
    return hasSim(context, true)
}

var hasSimCache: Boolean? = null
fun hasSim(context: Context, readCache: Boolean = true): Boolean {
    if (readCache && hasSimCache != null) {
        return hasSimCache!!
    }
    val manager = ContextCompat.getSystemService(context, TelephonyManager::class.java)
    hasSimCache = manager?.simState == TelephonyManager.SIM_STATE_READY
    return hasSimCache!!
}

fun getCellularOperatorType(context: Context): String {
    return getCellularOperatorType(context, true)
}

var cellularOperatorTypeCache = ""
fun getCellularOperatorType(context: Context, readCache: Boolean = true): String {
    if (readCache && cellularOperatorTypeCache.isNotEmpty()) {
        return cellularOperatorTypeCache
    }
    var cellularOperatorTypeCache = "网络未知"
    if (!networkIsConnect(context)) {
        cellularOperatorTypeCache = "网络没有连接"
        return cellularOperatorTypeCache
    }
    val networkType = checkNetworkTypeStr(context)
    if (!hasSim(context)) {
        cellularOperatorTypeCache = "$networkType-没有sim卡"
        return cellularOperatorTypeCache
    }
    val manager = ContextCompat.getSystemService(context, TelephonyManager::class.java)
        ?: return cellularOperatorTypeCache
    cellularOperatorTypeCache = "${manager.simOperatorName}-$networkType"
    return cellularOperatorTypeCache
}