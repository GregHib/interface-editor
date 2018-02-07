package com.greg.controller.utils

import java.io.File
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.text.DecimalFormat
import java.util.*

object BSPUtils {

    fun getFilePrefix(file: File) : String {
        val name = file.name
        return if (name.lastIndexOf(".") != -1) name.substring(0, name.lastIndexOf(".")) else name
    }

    fun sortFiles(files: Array<File>) {
        Arrays.sort(files, { first, second->
            val fid = Integer.parseInt(first.name.substring(0, first.name.lastIndexOf(".")))
            val sid = Integer.parseInt(second.name.substring(0, second.name.lastIndexOf(".")))
            Integer.compare(fid, sid)
        })
    }

    fun readableFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }

    fun isValidImage(file: File): Boolean {
        if (file.isDirectory) {
            return false
        }

        FileChannel.open(file.toPath()).use { channel -> return isPNG(channel) || isGIF(channel) || isJPG(channel) }
    }

    fun isPNG(channel: FileChannel): Boolean {
        val signatureBuf = ByteBuffer.allocate(8)

        channel.position(0)
        channel.read(signatureBuf)

        val signature = signatureBuf.array()
        return ((signature[0].toInt() and 0xFF) == 137) && signature[1].toInt() == 80 && signature[2].toInt() == 78 && signature[3].toInt() == 71 && signature[4].toInt() == 13 && signature[5].toInt() == 10 && signature[6].toInt() == 26 && signature[7].toInt() == 10
    }

    fun isGIF(channel: FileChannel): Boolean {
        val signatureBuf = ByteBuffer.allocate(3)

        channel.position(0)
        channel.read(signatureBuf)

        val signature = signatureBuf.array()
        return signature[0].toChar() == 'G' && signature[1].toChar() == 'I' && signature[2].toChar() == 'F'
    }

    fun isJPG(channel: FileChannel): Boolean {
        val signatureBuf = ByteBuffer.allocate(4)

        channel.position(0)
        channel.read(signatureBuf)

        val signature = signatureBuf.array()
        return ((signature[0].toInt() and 0xFF) shl 24) or ((signature[1].toInt() and 0xFF) shl 16) or ((signature[2].toInt() and 0xFF) shl 8) or (signature[3].toInt() and 0xFF) == -0x270020
    }

    fun launchURL(url: String) {
        val osName = System.getProperty("os.name")
        try {
            if (osName.startsWith("Mac OS")) {
                val fileMgr = Class.forName("com.apple.eio.FileManager")
                val openURL = fileMgr.getDeclaredMethod("openURL", String::class.java)
                openURL.invoke(null, arrayOf<Any>(url))
            } else if (osName.startsWith("Windows"))
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url)
            else {
                val browsers = arrayOf("firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape", "safari")
                var browser: String? = null
                var count = 0
                while (count < browsers.size && browser == null) {
                    if (Runtime.getRuntime().exec(arrayOf("which", browsers[count]))
                            .waitFor() == 0)
                        browser = browsers[count]
                    count++
                }
                if (browser == null) {
                    throw Exception("Could not find web browser")
                } else
                    Runtime.getRuntime().exec(arrayOf(browser, url))
            }
        } catch (ex: Exception) {
            Dialogue.showWarning("Failed to open url.").showAndWait()
        }

    }

}