package com.qytech.kikidemo

import java.io.DataOutputStream

/**
 * Created by Jax on 2020/6/29.
 * Description :
 * Version : V1.0.0
 */

object ShellUtil {
    fun runShellCommand(command: String): Int? {
        var process: Process? = null
        var os: DataOutputStream? = null
        try {
            process = Runtime.getRuntime().exec("/system/bin/sh")
            os = DataOutputStream(process?.outputStream)
            os.write(command.toByteArray())
            os.writeBytes("\n")
            os.flush()
            os.writeBytes("exit\n")
            os.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            os?.close()
        }
        return process?.waitFor()
    }
}
