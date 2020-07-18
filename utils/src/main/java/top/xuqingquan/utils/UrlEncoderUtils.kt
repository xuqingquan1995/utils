@file:JvmName("UrlEncoderUtils")

package top.xuqingquan.utils

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Created by 许清泉 on 2019/4/14 20:59
 */
/**
 * url解码
 * @param str 需要解码的参数
 * @param enc 编码方式
 */
fun decode(str: String, enc: String): String {
    return if (hasUrlEncoded(str)) {
        URLDecoder.decode(str, enc)
    } else {
        str
    }
}

/**
 * url解码，默认utf-8编码
 * @param str 需要解码的参数
 */
fun decode(str: String): String {
    return decode(str, StandardCharsets.UTF_8.name())
}

/**
 * url编码
 * @param str 需要编码的字符串
 * @param enc 编码方式
 * @param forceEncode 编码过的字符串是否强制编码
 */
fun encode(str: String, enc: String, forceEncode: Boolean = false): String {
    return if (forceEncode && hasUrlEncoded(str)) {
        URLEncoder.encode(str, enc)
    } else if (hasUrlEncoded(str)) {
        str
    } else {
        URLEncoder.encode(str, enc)
    }
}

/**
 * url编码
 * @param str 需要编码的字符串,默认utf-8编码
 * @param forceEncode 编码过的字符串是否强制编码
 */
fun encode(str: String, forceEncode: Boolean = false): String {
    return encode(str, StandardCharsets.UTF_8.name(), forceEncode)
}

/**
 * 判断 str 是否已经 URLEncoder.encode() 过
 * 经常遇到这样的情况, 拿到一个 URL, 但是搞不清楚到底要不要 URLEncoder.encode()
 * 不做 URLEncoder.encode() 吧, 担心出错, 做 URLEncoder.encode() 吧, 又怕重复了
 *
 * @param str 需要判断的内容
 * @return 返回 `true` 为被 URLEncoder.encode() 过
 */
fun hasUrlEncoded(str: String): Boolean {
    var encode = false
    for (i in str.indices) {
        val c = str[i]
        if (c == '%' && i + 2 < str.length) {
            // 判断是否符合urlEncode规范
            val c1 = str[i + 1]
            val c2 = str[i + 2]
            if (isValidHexChar(c1) && isValidHexChar(
                    c2
                )
            ) {
                encode = true
                break
            } else {
                break
            }
        }
    }
    return encode
}

/**
 * 判断 c 是否是 16 进制的字符
 *
 * @param c 需要判断的字符
 * @return 返回 `true` 为 16 进制的字符
 */
private fun isValidHexChar(c: Char): Boolean {
    return c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'
}