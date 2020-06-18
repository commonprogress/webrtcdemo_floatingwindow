package com.dongxl.fw

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.view.DragEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dongxl.fw.services.*
import com.dongxl.fw.utils.FloatingWindowUtils

class FloatWindowActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnSystemUiVisibilityChangeListener, View.OnDragListener, View.OnLongClickListener {
    private val FLOAT_PERMISSION_REQUEST_CODE_1: Int = 1
    private val FLOAT_PERMISSION_REQUEST_CODE_2: Int = 2
    private val FLOAT_PERMISSION_REQUEST_CODE_3: Int = 3
    private val FLOAT_PERMISSION_REQUEST_CODE_4: Int = 4
    private val FLOAT_PERMISSION_REQUEST_CODE_5: Int = 5
    private val FLOAT_PERMISSION_REQUEST_CODE_6: Int = 6
    private val FLOAT_PERMISSION_REQUEST_CODE_7: Int = 7
    private val FLOAT_PERMISSION_REQUEST_CODE_8: Int = 8

    private var button1: Button? = null
    private var button2: Button? = null
    private var button3: Button? = null
    private var button4: Button? = null
    private var button5: Button? = null
    private var button6: Button? = null
    private var button7: Button? = null
    private var button8: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_float_window)
        button1 = findViewById(R.id.button)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)

        button1?.setOnClickListener(this)
        button2?.setOnClickListener(this)
        button3?.setOnClickListener(this)
        button4?.setOnClickListener(this)
        button5?.setOnClickListener(this)
        button6?.setOnClickListener(this)
        button7?.setOnClickListener(this)
        button8?.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> clickButton1(true)
            R.id.button2 -> clickButton2(true)
            R.id.button3 -> clickButton3(true)
            R.id.button4 -> clickButton4(true)
            R.id.button5 -> clickButton5(true)
            R.id.button6 -> clickButton6(true)
            R.id.button7 -> clickButton7(true)
            R.id.button8 -> clickButton8(true)
        }
    }

    private fun clickButton1(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(this)) {
            startService(Intent(this, FloatingButtonService::class.java))
            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(this, FLOAT_PERMISSION_REQUEST_CODE_1)
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButton2(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(FloatWindowActivity@ this)) {
            startService(Intent(FloatWindowActivity@ this, FloatingImageService::class.java))
            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(FloatWindowActivity@ this, FLOAT_PERMISSION_REQUEST_CODE_2)
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButton3(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(this)) {
            startService(Intent(this, FloatingVideoService::class.java))
            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(this, FLOAT_PERMISSION_REQUEST_CODE_3)
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButton4(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(FloatWindowActivity@ this)) {
            startService(Intent(FloatWindowActivity@ this, TestServiceBindService::class.java))
            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(FloatWindowActivity@ this, FLOAT_PERMISSION_REQUEST_CODE_4)
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButton5(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(FloatWindowActivity@ this)) {

            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(FloatWindowActivity@ this, FLOAT_PERMISSION_REQUEST_CODE_5)
        } else {
            Toast.makeText(FloatWindowActivity@ this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    private var acToSeAidlInterface: IAcToSeAidlInterface? = null
    private var myServiceConnection: MyServiceConnection? = null

    internal inner class SeToAcAidlInterface : ISeToAcAidlInterface.Stub() {
        override fun onBindSuc() {
            TODO("Not yet implemented")
        }

        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
            TODO("Not yet implemented")
        }

    }

    //自定义ServiceConnection
    internal inner class MyServiceConnection : ServiceConnection {
        //当服务失去连接时调用
        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
            try {
                acToSeAidlInterface?.onUnbindSeToAc()
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
            acToSeAidlInterface = null
        }

        //当服务被成功连接的时候调用的方法
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            TODO("Not yet implemented")
            acToSeAidlInterface = IAcToSeAidlInterface.Stub.asInterface(service)
            try {
                acToSeAidlInterface?.onBindSeToAc(SeToAcAidlInterface())
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    private fun clickButton6(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(this)) {
            val intent = Intent()
            intent.setClass(this, TestKotlinBindService::class.java)
            myServiceConnection = MyServiceConnection()
            bindService(intent, myServiceConnection!!, Context.BIND_AUTO_CREATE)
            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(this, FLOAT_PERMISSION_REQUEST_CODE_6)
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButton7(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(this)) {

            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(this, FLOAT_PERMISSION_REQUEST_CODE_7)
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickButton8(isReq: Boolean) {
        if (FloatingWindowUtils.checkFloatPermission(this)) {

            return
        }
        if (isReq) {
            FloatingWindowUtils.applyFloatPermission(this, FLOAT_PERMISSION_REQUEST_CODE_8)
        } else {
            Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSystemUiVisibilityChange(visibility: Int) {
        TODO("Not yet implemented")
    }

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onLongClick(v: View?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            FLOAT_PERMISSION_REQUEST_CODE_1 -> clickButton1(false)
            FLOAT_PERMISSION_REQUEST_CODE_2 -> clickButton2(false)
            FLOAT_PERMISSION_REQUEST_CODE_3 -> clickButton3(false)
            FLOAT_PERMISSION_REQUEST_CODE_4 -> clickButton4(false)
            FLOAT_PERMISSION_REQUEST_CODE_5 -> clickButton5(false)
            FLOAT_PERMISSION_REQUEST_CODE_6 -> clickButton6(false)
            FLOAT_PERMISSION_REQUEST_CODE_7 -> clickButton7(false)
            FLOAT_PERMISSION_REQUEST_CODE_8 -> clickButton8(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myServiceConnection?.let { unbindService(it) }
    }
}
