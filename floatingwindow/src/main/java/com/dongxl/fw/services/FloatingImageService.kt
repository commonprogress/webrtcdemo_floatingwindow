package com.dongxl.fw.services

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.dongxl.fw.utils.FloatingWindowManager

class FloatingImageService : LifecycleService() {

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!FloatingWindowManager.instance.showUpdateFloatWindow(this)) {
            stopSelf(startId)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        FloatingWindowManager.instance.removeFloatWindowView()
        FloatingWindowManager.instance.destroy()
    }
}
