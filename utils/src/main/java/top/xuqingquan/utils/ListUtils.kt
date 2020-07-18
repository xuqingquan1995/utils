package top.xuqingquan.utils

import java.io.*


/**
 * Created by 许清泉 on 2019-07-18 14:07
 */
fun <E : Serializable> List<E>.deepCopy(): List<E> {
    val byteOut = ByteArrayOutputStream()
    val outs = ObjectOutputStream(byteOut)
    outs.writeObject(this)
    val byteIn = ByteArrayInputStream(byteOut.toByteArray())
    val ins = ObjectInputStream(byteIn)
    @Suppress("UNCHECKED_CAST")
    return ins.readObject() as List<E>
}