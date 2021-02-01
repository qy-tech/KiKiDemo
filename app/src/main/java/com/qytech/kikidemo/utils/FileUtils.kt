package com.qytech.kikidemo.utils

import android.content.Context
import android.os.storage.StorageManager
import android.text.TextUtils
import java.io.*
import java.lang.reflect.InvocationTargetException

object FileUtils {
    /**
     * 读取文件
     *
     * @param file
     * @return 字符串
     */
    fun readFromFile(file: File?): String? {
        if (file?.exists() != true) return ""
        return try {
            val fileInputStream = FileInputStream(file)
            val reader =
                BufferedReader(InputStreamReader(fileInputStream))
            val value = reader.readLine()
            fileInputStream.close()
            value
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }


    /**
     * 文件中写入字符串
     *
     * @param file
     * @param value
     */
    fun write2File(file: File?, value: String?): Boolean {
        if (file?.exists() != true) return false
        return try {
            val fileOutputStream = FileOutputStream(file)
            val pWriter = PrintWriter(fileOutputStream)
            pWriter.println(value)
            pWriter.flush()
            pWriter.close()
            fileOutputStream.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun getStoragePath(context: Context): String? {
        val mStorageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolumeClazz: Class<*>
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume")
            val getVolumeList = mStorageManager.javaClass.getMethod("getVolumeList")
            val getPath = storageVolumeClazz.getMethod("getPath")
            val isRemovable = storageVolumeClazz.getMethod("isRemovable")
            val result = getVolumeList.invoke(mStorageManager)
            val length = java.lang.reflect.Array.getLength(result)
            for (i in 0 until length) {
                val storageVolumeElement = java.lang.reflect.Array.get(result, i)
                val path = getPath.invoke(storageVolumeElement) as String
                val removable = isRemovable.invoke(storageVolumeElement) as Boolean
                if (removable && !TextUtils.isEmpty(path)) {
                    return path.replace("/storage", "/mnt/media_rw")
//                    return path
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }
}