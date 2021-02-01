package com.qytech.kikidemo.ui.main

import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.qytech.kikidemo.GlobalApplication
import com.qytech.kikidemo.R
import com.qytech.kikidemo.ShellUtil
import com.qytech.kikidemo.utils.FileUtils
import com.qytech.kikidemo.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class MainViewModel : ViewModel() {

    companion object {
        val COMMAND_START_RECORD =
            "tinycap ${GlobalApplication.instance().applicationContext.filesDir.path}/${System.currentTimeMillis()}.wav -D 1 -d 0 -c 4 -r 16000 -b 16"

        const val COMMAND_STOP_RECORD = "kill -2 $(pidof tinycap)"

        const val FAN_CONTROL_PWM = "/sys/devices/platform/pwm-fan/hwmon/hwmon1/pwm1"

        init {
            System.loadLibrary("native-lib")
        }

    }

    private var recordJob: Job? = null

    private external fun spiTest()

    fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_record -> {
                recordJob = viewModelScope.launch(Dispatchers.IO) {
                    Timber.d("onClick message: btn_start_record")
                    val exitCode = ShellUtil.runShellCommand(COMMAND_START_RECORD)
                    Timber.d("start record exitCode $exitCode")
                }
                recordJob?.start()
            }
            R.id.btn_stop_record -> {
                recordJob?.cancel()
                viewModelScope.launch {
                    Timber.d("onClick message: btn_stop_record ")
                    val exitCode = ShellUtil.runShellCommand(COMMAND_STOP_RECORD)
                    Timber.d("stop record exitCode $exitCode")
                }
            }
            R.id.btn_spi_led -> {
                viewModelScope.launch {
                    spiTest()
                }
            }
            R.id.btn_navigation_camera -> {
                Navigation.findNavController(v).navigate(R.id.action_main_to_camera)
            }
        }
    }

    fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        Timber.d("onProgressChanged message:  progress $progress fromUser $fromUser")
        FileUtils.write2File(File(FAN_CONTROL_PWM), progress.toString())
    }

}