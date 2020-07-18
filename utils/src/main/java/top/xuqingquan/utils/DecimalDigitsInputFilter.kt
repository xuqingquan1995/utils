package top.xuqingquan.utils

import android.text.InputFilter
import android.text.Spanned

/**
 * Created by 许清泉 on 2019-07-10 10:55
 * 小数输入限制，第一个字符是小数点则添加0.
 * @param decimalDigits 限制小数输入长度
 * @param maxLength 限制总长度
 */
class DecimalDigitsInputFilter(private val decimalDigits: Int, private val maxLength: Int = Int.MAX_VALUE) :
    InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        var dotPos = -1
        val len = dest.length
        if (len >= maxLength) {
            return ""
        }
        if (dest.contains(".")) {
            dotPos = dest.indexOf('.')
        }
        if (source == "." && len == 0) {
            return "0."
        }
        if (dotPos >= 0) {
            if (source == ".") {
                return ""
            }
            if (dend <= dotPos) {
                return null
            }
            if (len - dotPos > decimalDigits) {
                return ""
            }
        }
        return null
    }
}
