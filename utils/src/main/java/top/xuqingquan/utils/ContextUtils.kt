package top.xuqingquan.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup
import java.io.Serializable

inline val Context.defaultSharedPreferences: SharedPreferences
    get() = getSharedPreferences("${packageName}_preferences", Context.MODE_PRIVATE)

inline val Fragment.defaultSharedPreferences: SharedPreferences?
    get() = activity?.getSharedPreferences("${activity?.packageName}_preferences", Context.MODE_PRIVATE)

inline val Context.ctx: Context
    get() = this

inline val Activity.act: Activity
    get() = this

/**
 * Returns the content view of this Activity if set, null otherwise.
 */
inline val Activity.contentView: View?
    get() = findOptional<ViewGroup>(android.R.id.content)?.getChildAt(0)

inline fun <reified T : View> View.find(@IdRes id: Int): T = findViewById(id)
inline fun <reified T : View> Activity.find(@IdRes id: Int): T = findViewById(id)
inline fun <reified T : View> Fragment.find(@IdRes id: Int): T = view?.findViewById(id) as T
inline fun <reified T : View> Dialog.find(@IdRes id: Int): T = findViewById(id)

inline fun <reified T : View> View.findOptional(@IdRes id: Int): T? = findViewById(id) as? T
inline fun <reified T : View> Activity.findOptional(@IdRes id: Int): T? = findViewById(id) as? T
inline fun <reified T : View> Fragment.findOptional(@IdRes id: Int): T? = view?.findViewById(id) as? T
inline fun <reified T : View> Dialog.findOptional(@IdRes id: Int): T? = findViewById(id) as? T

@Suppress("NOTHING_TO_INLINE")
inline fun <T: Fragment> T.withArguments(vararg params: Pair<String, Any?>): T {
    arguments = bundleOf(*params)
    return this
}

fun bundleOf(vararg params: Pair<String, Any?>): Bundle {
    val b = Bundle()
    for (p in params) {
        val (k, v) = p
        when (v) {
            null -> b.putSerializable(k, null)
            is Boolean -> b.putBoolean(k, v)
            is Byte -> b.putByte(k, v)
            is Char -> b.putChar(k, v)
            is Short -> b.putShort(k, v)
            is Int -> b.putInt(k, v)
            is Long -> b.putLong(k, v)
            is Float -> b.putFloat(k, v)
            is Double -> b.putDouble(k, v)
            is String -> b.putString(k, v)
            is CharSequence -> b.putCharSequence(k, v)
            is Parcelable -> b.putParcelable(k, v)
            is Serializable -> b.putSerializable(k, v)
            is BooleanArray -> b.putBooleanArray(k, v)
            is ByteArray -> b.putByteArray(k, v)
            is CharArray -> b.putCharArray(k, v)
            is DoubleArray -> b.putDoubleArray(k, v)
            is FloatArray -> b.putFloatArray(k, v)
            is IntArray -> b.putIntArray(k, v)
            is LongArray -> b.putLongArray(k, v)
            is Array<*> -> {
                @Suppress("UNCHECKED_CAST")
                when {
                    v.isArrayOf<Parcelable>() -> b.putParcelableArray(k, v as Array<out Parcelable>)
                    v.isArrayOf<CharSequence>() -> b.putCharSequenceArray(k, v as Array<out CharSequence>)
                    v.isArrayOf<String>() -> b.putStringArray(k, v as Array<out String>)
                    else -> throw Exception("Unsupported bundle component (${v.javaClass})")
                }
            }
            is ShortArray -> b.putShortArray(k, v)
            is Bundle -> b.putBundle(k, v)
            else -> throw Exception("Unsupported bundle component (${v.javaClass})")
        }
    }

    return b
}

inline val Context.displayMetrics: android.util.DisplayMetrics
    get() = resources.displayMetrics

inline val Context.configuration: Configuration
    get() = resources.configuration

inline val Configuration.portrait: Boolean
    get() = orientation == Configuration.ORIENTATION_PORTRAIT

inline val Configuration.landscape: Boolean
    get() = orientation == Configuration.ORIENTATION_LANDSCAPE

inline val Configuration.long: Boolean
    get() = (screenLayout and Configuration.SCREENLAYOUT_LONG_YES) != 0
