package io.nshusa.rsam.util

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream

import java.*
import java.io.*
import java.nio.ByteBuffer
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * A utility class for performing compression/decompression.
 *
 * @author Graham
 */
object CompressionUtils {

    /**
     * Bzip2s the specified array, removing the header.
     *
     * @param uncompressed The uncompressed array.
     * @return The compressed array.
     * @throws IOException If there is an error compressing the array.
     */
    @Throws(IOException::class)
    fun bzip2(uncompressed: ByteArray): ByteArray {
        val bout = ByteArrayOutputStream()

        BZip2CompressorOutputStream(bout, 1).use { os ->
            os.write(uncompressed)
            os.finish()

            val compressed = bout.toByteArray()
            val newCompressed = ByteArray(compressed.size - 4) // Strip the header
            System.arraycopy(compressed, 4, newCompressed, 0, newCompressed.size)
            return newCompressed
        }
    }

    /**
     * Debzip2s the compressed array and places the result into the decompressed array.
     *
     * @param compressed   The compressed array, **without** the header.
     * @param decompressed The decompressed array.
     * @throws IOException If there is an error decompressing the array.
     */
    @Throws(IOException::class)
    fun debzip2(compressed: ByteArray, decompressed: ByteArray) {
        val newCompressed = ByteArray(compressed.size + 4)
        newCompressed[0] = 'B'.toByte()
        newCompressed[1] = 'Z'.toByte()
        newCompressed[2] = 'h'.toByte()
        newCompressed[3] = '1'.toByte()
        System.arraycopy(compressed, 0, newCompressed, 4, compressed.size)

        DataInputStream(BZip2CompressorInputStream(ByteArrayInputStream(newCompressed))).use { `is` -> `is`.readFully(decompressed) }
    }

    /**
     * Degzips the compressed array and places the results into the decompressed array.
     *
     * @param compressed   The compressed array.
     * @param decompressed The decompressed array.
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    fun degzip(compressed: ByteArray, decompressed: ByteArray) {
        DataInputStream(GZIPInputStream(ByteArrayInputStream(compressed))).use { `is` -> `is`.readFully(decompressed) }
    }

    /**
     * Degzips **all** of the datain the specified [ByteBuffer].
     *
     * @param compressed The compressed buffer.
     * @return The decompressed array.
     * @throws IOException If there is an error decompressing the buffer.
     */
    @Throws(IOException::class)
    fun degzip(compressed: ByteBuffer): ByteArray {
        GZIPInputStream(ByteArrayInputStream(compressed.array())).use { `is` ->
            ByteArrayOutputStream().use { out ->
                val buffer = ByteArray(1024)

                while (true) {
                    val read = `is`.read(buffer, 0, buffer.size)
                    if (read == -1) {
                        break
                    }

                    out.write(buffer, 0, read)
                }

                return out.toByteArray()
            }
        }
    }

    /**
     * Gzips the specified array.
     *
     * @param uncompressed The uncompressed array.
     * @return The compressed array.
     * @throws IOException If there is an error compressing the array.
     */
    @Throws(IOException::class)
    fun gzip(uncompressed: ByteArray): ByteArray {
        val bout = ByteArrayOutputStream()

        GZIPOutputStream(bout).use { os ->
            os.write(uncompressed)
            os.finish()
            return bout.toByteArray()
        }
    }

}
/**
 * Default private constructor to prevent instantiation.
 */