@file:JvmName("ViewUtils")

package top.xuqingquan.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.View
import java.io.File
import java.io.FileOutputStream

/**
 * Created by 许清泉 on 2019-04-19 22:30
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
    //获取view的长宽
    val width = view.measuredWidth
    val height = view.measuredHeight
    //若传入的view长或宽为小于等于0，则返回，不生成图片
    if (width <= 0 || height <= 0) {
        return null
    }
    //生成一个ARGB8888的bitmap，宽度和高度为传入view的宽高
    val bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) ?: return null
    //根据bitmap生成一个画布
    val canvas = Canvas(bm)
    //注意：这里是解决图片透明度问题，给底色上白色，若存储时保存的为png格式的图，则无需此步骤
    canvas.drawColor(Color.WHITE)
    //手动将这个视图渲染到指定的画布上
    view.draw(canvas)
    return bm
}

/**
 * 将bitmap保存成图片
 */
fun bitmap2File(context: Context?, bmp: Bitmap): File? {
    if (context == null) {
        return null
    }
    // 判断是否可以对SDcard进行操作
    val sdCardDir = context.getExternalFilesDir("images")!!.absolutePath
    //目录转化成文件夹
    val dirFile = File(sdCardDir)
    //如果不存在，那就建立这个文件夹
    if (!dirFile.exists()) {
        dirFile.mkdirs()
    }
    // 在SDcard的目录下创建图片文,以当前时间为其命名，注意文件后缀，若生成为JPEG则为.jpg,若为PNG则为.png
    val file = File(sdCardDir, System.currentTimeMillis().toString() + ".jpg")
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(file)
        //将bitmap（数值100表示不压缩）存储到out输出流中去，图片格式为JPEG
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
        //bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    try {
        out!!.flush()
        out.close()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return file
}