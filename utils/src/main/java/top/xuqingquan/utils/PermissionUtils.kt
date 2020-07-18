@file:JvmName("PermissionUtils")

package top.xuqingquan.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.AppOpsManagerCompat
import android.text.TextUtils
import android.support.v4.content.ContextCompat
import java.util.*

/**
 * Created by 许清泉 on 2019-05-27 01:18
 */

fun hasPermission(context: Context, vararg permissions: String): Boolean {
    return hasPermission(context.applicationContext, listOf(*permissions))
}


fun hasPermission(ctx: Context, permissions: List<String>): Boolean {
    val context = ctx.applicationContext
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    for (permission in permissions) {
        var result = ContextCompat.checkSelfPermission(context, permission)
        if (result == PackageManager.PERMISSION_DENIED) {
            return false
        }
        val op = AppOpsManagerCompat.permissionToOp(permission)
        if (TextUtils.isEmpty(op)) {
            continue
        }
        result = AppOpsManagerCompat.noteProxyOp(context, op!!, context.packageName)
        if (result != AppOpsManagerCompat.MODE_ALLOWED) {
            return false
        }
    }
    return true
}


fun getDeniedPermissions(context: Context, permissions: Array<String>?): List<String> {
    if (permissions.isNullOrEmpty()) {
        return emptyList()
    }
    val deniedPermissions = ArrayList<String>()
    for (permission in permissions) {
        if (!hasPermission(context, permission)) {
            deniedPermissions.add(permission)
        }
    }
    return deniedPermissions
}