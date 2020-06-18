package com.dongxl.fw.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException
import com.dongxl.fw.IAcToSeAidlInterface
import com.dongxl.fw.ISeToAcAidlInterface

class TestKotlinBindService : Service() {

    private var seToAcAidlInterface: ISeToAcAidlInterface? = null
    private var isBindService = false

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
        return AcToSeAidlInterface()
    }

    internal inner class AcToSeAidlInterface : IAcToSeAidlInterface.Stub() {
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
            TODO("Not yet implemented")
        }

        override fun onBindSeToAc(aidlInterface: ISeToAcAidlInterface?) {
            TODO("Not yet implemented")
            isBindService = true
            seToAcAidlInterface = aidlInterface
            try {
                seToAcAidlInterface?.onBindSuc()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onUnbindSeToAc() {
            TODO("Not yet implemented")
            isBindService = false
            seToAcAidlInterface = null
        }

    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBindService = false
        seToAcAidlInterface = null
//        return super.onUnbind(intent)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
