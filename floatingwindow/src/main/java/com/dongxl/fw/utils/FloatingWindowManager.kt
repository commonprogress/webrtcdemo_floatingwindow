package com.dongxl.fw.utils

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.dongxl.fw.R


class FloatingWindowManager private constructor() {
    companion object {
        val instance: FloatingWindowManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FloatingWindowManager()
        }
    }

    private val LONG_CLICK_LIMIT: Long = 100
    private lateinit var mContext: Context
    private var mWindowManager: WindowManager? = null
    private var mLayoutParams: WindowManager.LayoutParams? = null
    private var mFloatView: View? = null

    private val mHandler: Handler by lazy { Handler() }

    // 控制的变量
    private var lastDownX: Float = 0f  // 控制的变量
    private var lastDownY: Float = 0f
    private var lastParamX: Int = 0
    private var lastParamY: Int = 0
    private var isTouching: Boolean = false//是否触摸中
    private var isTouchMoveing: Boolean = false//是否移动中
    private var isLongTouch: Boolean = false//是否长按中
    private var lastDownTime: Long = 0

    private fun initWindowManager() {
        if (null == mWindowManager) {
            mWindowManager = mContext.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        }
    }

    private fun initLayoutParams() {
        if (null != mLayoutParams) {
            return
        }
        mLayoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        //刘海屏延伸到刘海里面
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mLayoutParams!!.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        mLayoutParams!!.packageName = mContext.applicationContext.packageName
        mLayoutParams!!.format = PixelFormat.RGBA_8888
        // FLAG_NOT_FOCUSABLE 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        // 当悬浮窗显示的时候可以获取到焦点
//        mLayoutParams!!.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
//                or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)

        mLayoutParams!!.width = WindowManager.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.height = WindowManager.LayoutParams.WRAP_CONTENT

        mLayoutParams!!.gravity = Gravity.LEFT or Gravity.TOP //默认剧中
        mLayoutParams!!.x = 300//设置gravity之后的再次偏移的显示的位置
        mLayoutParams!!.y = 300//
    }

    fun updateLayoutParams() {
        if (null != mLayoutParams) {
            mLayoutParams!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        }
        if (null != mFloatView) {
            mWindowManager?.addView(mFloatView, mLayoutParams)
        }
    }

    fun showUpdateFloatWindow(context: Context): Boolean {
        this.mContext = context.applicationContext
        if (FloatingWindowUtils.checkFloatPermission(mContext)) {
            if (isShowFloating()) {
                updateFloatWindowView()
            } else {
                showFloatWindowView()
            }
            return true
        }
        return false
    }

    private fun isShowFloating(): Boolean {
        return null != mWindowManager && null != mLayoutParams && null != mFloatView
    }

    private fun initFloatWindowView() {
        mFloatView = LayoutInflater.from(mContext).inflate(R.layout.float_window_button, null)
        val button1 = mFloatView?.findViewById<Button>(R.id.float_test_button1)
        val button2 = mFloatView?.findViewById<Button>(R.id.float_test_button2)
        button1?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(v?.context, "监听写法一", Toast.LENGTH_SHORT).show()
            }
        })
        button2?.setOnClickListener({
            Toast.makeText(it?.context, "监听写法二", Toast.LENGTH_SHORT).show()
        })//加不加小括号都正确

//        button2?.setOnClickListener(){ v: View ->
//            Toast.makeText(v?.context, "监听写法三", Toast.LENGTH_SHORT).show()
//        }//最后一个参数是一个函数的话，可以直接把括号的实现提到圆括号外面
//
//        button2?.setOnClickListener ({ v ->
//            Toast.makeText(v?.context, "监听写法四", Toast.LENGTH_SHORT).show()
//        })

        button1?.isEnabled = false
        button1?.isClickable = false
        button2?.isEnabled = false
        button2?.isClickable = false
        mFloatView?.setOnTouchListener(OnDragTouchListener())
    }

    private fun showFloatWindowView() {
        initWindowManager()
        initFloatWindowView()
        initLayoutParams()
        mWindowManager?.addView(mFloatView, mLayoutParams)
    }

    private fun updateFloatWindowView() {
//        if (isShowFloating()) {
//            mWindowManager?.updateViewLayout(mFloatView, mLayoutParams)
//        }
    }

    fun removeFloatWindowView() {
        if (isShowFloating()) {
            mHandler.removeCallbacksAndMessages(null)
            mWindowManager?.removeView(mFloatView)
        }
    }

    internal inner class OnDragTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            Log.d("FloatingButtonService", "onTouch event?.actionMasked:${event?.actionMasked}, event.rawX:${event?.rawX} ,downX:$lastDownX, event.rawY:${event?.rawY}  ,downY:$lastDownY,")
            if (null == mLayoutParams) {
                return true
            }
            when (event?.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    isTouching = true
                    lastDownTime = System.currentTimeMillis()
                    //手指按下的位置
                    lastDownX = event.rawX
                    lastDownY = event.rawY
                    //记录手指按下时,悬浮窗的位置
                    lastParamX = mLayoutParams!!.x
                    lastParamY = mLayoutParams!!.y
                    mHandler.postDelayed({
                        if (isLongTouch()) {
                            isLongTouch = true
                        }
                    }, LONG_CLICK_LIMIT)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!isLongTouch && isTouchSlop(event)) {
                        return true
                    }
                    if (isLongTouch) {
                        var moveX = event.rawX - lastDownX
                        var moveY = event.rawY - lastDownY
                        updateFloatPosition(moveX, moveY)
                        isTouchMoveing = true
                    }
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    isTouching = false
                    if (isLongTouch) {
                        isLongTouch = false
                    }
                    isTouchMoveing = false
                    //当手指按下的位置和手指抬起来的位置距离小于10像素时,将此次触摸归结为点击事件,
                    if (isTouchSlop(event)) {
                        callOnClick()
                    }
                }
            }
            return true
        }
    }

    private fun isLongTouch(): Boolean {
        val time = System.currentTimeMillis()
        return isTouching && !isTouchMoveing && time - lastDownTime >= LONG_CLICK_LIMIT
    }

    private fun isTouchSlop(event: MotionEvent): Boolean {
        val x = event.rawX
        val y = event.rawY
        val mTouchSlop = 10
        return Math.abs(x - lastDownX) < mTouchSlop && Math.abs(y - lastDownY) < mTouchSlop
    }

    private fun callOnClick() {
        Log.d("FloatingButtonService", "callOnClick ")
    }

    private fun updateFloatPosition(moveX: Float, moveY: Float) {
        if (null != mLayoutParams && null != mWindowManager && null != mFloatView) {
            mLayoutParams!!.x = lastParamX + moveX.toInt()
            mLayoutParams!!.y = lastParamY + moveY.toInt()
            mWindowManager!!.updateViewLayout(mFloatView, mLayoutParams)
        }
    }

    fun destroy() {
        mHandler.removeCallbacksAndMessages(null)
        mWindowManager = null
        mLayoutParams = null
        mFloatView = null
    }

}