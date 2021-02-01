package com.qytech.kikidemo

import android.app.Application
import timber.log.Timber
import kotlin.properties.Delegates

/**
 * Created by Jax on 2021/1/28.
 * Description :
 * Version : V1.0.0
 */
class GlobalApplication : Application() {

    companion object {
        private var instance: GlobalApplication by Delegates.notNull()
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}