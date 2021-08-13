package top.xuqingquan.utils

import android.view.View

/**
 *  @author : 许清泉 xuqingquan1995@gmail.com
 *  @since   : 2021-08-13
 */

fun View.noFastClick(time: Long = 500, action: (v: View) -> Unit) {
    setOnClickListener {
        Timber.d("锁定点击-${this}")
        isClickable = false
        action(it)
        postDelayed({
            isClickable = true
            Timber.d("释放点击-${this}")
        }, time)
    }
}