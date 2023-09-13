@file:JvmName("ViewUtils")

package top.xuqingquan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.io.File
import java.io.FileOutputStream

/**
 * @author 许清泉 on 2019-04-19 22:30
 */

/**
 * 将视图保存成文件
 */
fun saveView2File(view: View?): String? {
    return view2Bitmap(view)?.let {
        bitmap2File(view?.context, it)
    }?.absolutePath
}

/**
 * 将视图保存成bitmap
 */
fun view2Bitmap(view: View?): Bitmap? {
    if (view == null) {
        return null
    }
    view.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

/**
 * 将bitmap保存成图片
 * @param context 上下文
 * @param bmp 要保存的bitmap
 * @param outDir 要保存的路径
 * @param ext 要保存的文件后缀
 * @param quality 压缩率
 */
fun bitmap2File(
    context: Context?, bmp: Bitmap, outDir: File? = null, ext: String = "png", quality: Int = 100
): File? {
    if (context == null) {
        return null
    }
    val file: File
    if (outDir == null) {
        // 判断是否可以对SDcard进行操作
        val sdCardDir = context.getExternalFilesDir("images")!!.absolutePath
        //目录转化成文件夹
        val dirFile = File(sdCardDir)
        //如果不存在，那就建立这个文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs()
        }
        // 在SDcard的目录下创建图片文,以当前时间为其命名，注意文件后缀，若生成为JPEG则为.jpg,若为PNG则为.png
        file = File(sdCardDir, "${System.currentTimeMillis()}.${ext}")
    } else if (outDir.absolutePath.endsWith("/")) {//是一个文件夹地址
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        file = File(outDir, "${System.currentTimeMillis()}.${ext}")
    } else {
        if (outDir.parentFile?.exists() == false) {
            outDir.parentFile?.mkdirs()
        }
        file = outDir
    }
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(file)
        //将bitmap（数值100表示不压缩）存储到out输出流中去，图片格式为JPEG
        val format = if (ext == "png"){
            Bitmap.CompressFormat.PNG
        }else{
            Bitmap.CompressFormat.JPEG
        }
        bmp.compress(format, quality, out)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    try {
        out!!.flush()
        out.close()
        bmp.recycle()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return file
}