@file:JvmName("ApkUtils")

package top.xuqingquan.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import java.io.File

/**
 * Create by 许清泉 on 2020/6/23 22:58
 */

/**
 * 安装应用
 *
 * @param context
 * @param file
 */
fun installAPK(context: Context, file: File?) {
    if (file == null || !file.exists()) return
    val type = "application/vnd.android.package-archive"
    val intent = Intent(Intent.ACTION_VIEW)
    // 由于没有在Activity环境下启动Activity,设置下面的标签
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val contentUri = ScaffoldFileProvider.getUriForFile(
            context, context.packageName + ".ScaffoldFileProvider", file
        )
        Timber.d("installApk: $contentUri")
        intent.setDataAndType(contentUri, type)
    } else {
        intent.setDataAndType(Uri.fromFile(file), type)
    }
    context.startActivity(intent)
}