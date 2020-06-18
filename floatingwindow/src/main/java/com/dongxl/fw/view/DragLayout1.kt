package com.dongxl.fw.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper

class DragLayout1 : FrameLayout {
    private val TAG: String = DragLayout1::class.java.simpleName
    private lateinit var mContext: Context

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    private fun init(context: Context) {
        this.mContext = context.applicationContext
        /**
         * @param ViewGroup forParent 必须是一个ViewGroup
         * @param float sensitivity 灵敏度
         * @param Callback cb 回调
         */
//        mDragHelper = ViewDragHelper.create(this, 1.0F, ViewDragCallback())
    }
}