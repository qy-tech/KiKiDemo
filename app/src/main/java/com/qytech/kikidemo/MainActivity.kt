package com.qytech.kikidemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.qytech.kikidemo.ui.main.MainFragment
import com.qytech.kikidemo.utils.showToast
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    companion object {
        val sensorKeys = mapOf(
            61 to "61",
            62 to "62",
            63 to "63",
            64 to "64",
            65 to "65",
            66 to "66",
            67 to "67",
            68 to "68"
        )
        val PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        const val REQUEST_CODE_PERMISSION = 0x01
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (checkPermission()) {
            requestPermissions()
        }
    }

    private fun checkPermission(): Boolean {
        return PERMISSIONS.any {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.any { it == PackageManager.PERMISSION_DENIED }) {
            requestPermissions()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Timber.d("onKeyDown message:  keyCode $keyCode scanCode ${sensorKeys[event?.scanCode]}")
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Timber.d("onKeyUp message:  keyCode $keyCode scanCode ${sensorKeys[event?.scanCode]}")
        return super.onKeyUp(keyCode, event)
    }
}