@file:JvmName("ZipHelper")

package top.xuqingquan.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.nio.charset.Charset
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import java.util.zip.Inflater

/**
 * @author 许清泉 xuqingquan1995@gmail.com
 * @since 2021/8/6-17:00
 */
fun closeQuietly(closeable: Closeable?) {
    try {
        closeable?.close()
    } catch (ignored: Throwable) {
    }
}

/**
 * zlib decompress 2 byte
 *
 * @param bytesToDecompress byte
 * @return result
 */
fun decompressForZlib(bytesToDecompress: ByteArray): ByteArray? {
    var returnValues: ByteArray? = null
    val inflater = Inflater()
    val numberOfBytesToDecompress = bytesToDecompress.size
    inflater.setInput(bytesToDecompress, 0, numberOfBytesToDecompress)
    val bytesDecompressedSoFar = arrayListOf<Byte>()
    try {
        while (!inflater.needsInput()) {
            val bytesDecompressedBuffer = ByteArray(numberOfBytesToDecompress)
            val numberOfBytesDecompressedThisTime = inflater.inflate(bytesDecompressedBuffer)
            for (b in 0 until numberOfBytesDecompressedThisTime) {
                bytesDecompressedSoFar.add(bytesDecompressedBuffer[b])
            }
        }
        returnValues = bytesDecompressedSoFar.toByteArray()
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    inflater.end()
    return returnValues
}

/**
 * zlib decompress 2 String
 *
 * @param bytesToDecompress byte
 * @param charsetName charset
 * @return result
 */
fun decompressToStringForZlib(bytesToDecompress: ByteArray, charsetName: Charset): String? {
    val bytesDecompressed = decompressForZlib(bytesToDecompress) ?: return null
    var returnValue: String? = null
    try {
        returnValue =
            String(bytesDecompressed, 0, bytesDecompressed.size, charsetName)
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return returnValue
}

/**
 * zlib decompress 2 String
 *
 * @param bytesToDecompress byte
 * @return result
 */
fun decompressToStringForZlib(bytesToDecompress: ByteArray): String? {
    return decompressToStringForZlib(bytesToDecompress, Charsets.UTF_8)
}

/**
 * zlib compress 2 byte
 *
 * @param bytesToCompress byte
 * @return result
 */
fun compressForZlib(bytesToCompress: ByteArray): ByteArray? {
    try {
        val deflater = Deflater()
        deflater.setInput(bytesToCompress)
        deflater.finish()
        val bytesCompressed = ByteArray(Short.MAX_VALUE.toInt())
        val numberOfBytesAfterCompression = deflater.deflate(bytesCompressed)
        val returnValues = ByteArray(numberOfBytesAfterCompression)
        System.arraycopy(bytesCompressed, 0, returnValues, 0, numberOfBytesAfterCompression)
        return returnValues
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return null
}

/**
 * zlib compress 2 byte
 *
 * @param stringToCompress str
 * @return result
 */
fun compressForZlib(stringToCompress: String): ByteArray? {
    return compressForZlib(stringToCompress.toByteArray(Charsets.UTF_8))
}

/**
 * gzip compress 2 byte
 *
 * @param bytesToCompress ByteArray
 * @return result
 */
fun compressForGzip(bytesToCompress: ByteArray): ByteArray? {
    var bis: ByteArrayInputStream? = null
    var bos: ByteArrayOutputStream? = null
    var gos: GZIPOutputStream? = null
    try {
        bis = ByteArrayInputStream(bytesToCompress)
        bos = ByteArrayOutputStream(bytesToCompress.size)
        gos = GZIPOutputStream(bos)
        bis.copyTo(gos)
    } catch (t: Throwable) {
        t.printStackTrace()
    } finally {
        closeQuietly(bis)
        closeQuietly(gos)
    }
    return bos?.toByteArray()
}

/**
 * gzip compress 2 byte
 *
 * @param content String
 * @return result
 */
fun compressForGzip(content: String): ByteArray? {
    return compressForGzip(content.toByteArray(Charsets.UTF_8))
}

/**
 * gzip decompress 2 string
 *
 * @param compressed byte
 * @param charsetName charset
 * @return result
 */
fun decompressForGzip(compressed: ByteArray, charsetName: Charset): String? {
    val bufferSize = compressed.size
    var gis: GZIPInputStream? = null
    var bis: ByteArrayInputStream? = null
    try {
        bis = ByteArrayInputStream(compressed)
        gis = GZIPInputStream(bis, bufferSize)
        val sb = StringBuilder()
        val data = ByteArray(bufferSize)
        var bytesRead: Int
        while (gis.read(data).also { bytesRead = it } != -1) {
            sb.append(String(data, 0, bytesRead, charsetName))
        }
        return sb.toString()
    } catch (e: Throwable) {
        e.printStackTrace()
    } finally {
        closeQuietly(gis)
        closeQuietly(bis)
    }
    return null
}

/**
 * gzip decompress 2 string
 *
 * @param compressed byte
 * @return result
 */
fun decompressForGzip(compressed: ByteArray): String? {
    return decompressForGzip(compressed, Charsets.UTF_8)
}