package com.dongxl.fw.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper


/**
 * TODO: document your custom view class.
 */
class DragLayout : FrameLayout {

    private val TAG: String = DragLayout::class.java.simpleName
    private lateinit var mContext: Context
    private var mDragHelper: ViewDragHelper? = null

    //true left, false right
    private var leftOrRight = false

    //
    private var childView: View? = null

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
        this.mContext = context
        /**
         * @param ViewGroup forParent 必须是一个ViewGroup
         * @param float sensitivity 灵敏度
         * @param Callback cb 回调
         */
        mDragHelper = ViewDragHelper.create(this, 1.0F, ViewDragCallback())
    }

    internal inner class ViewDragCallback : ViewDragHelper.Callback() {
        /**
         * 尝试捕获子view，一定要返回true
         *
         * @param view      尝试捕获的view
         * @param pointerId 指示器id？
         *                  这里可以决定哪个子view可以拖动
         */
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            Log.d(TAG, "tryCaptureView child:$child, childView:$childView")
//            return true
            return child == childView
        }

        /**
         * 处理水平方向上的拖动
         *
         * @param child 被拖动到view
         * @param left  移动到达的x轴的距离
         * @param dx    建议的移动的x距离
         */
        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            Log.d(TAG, "clampViewPositionHorizontal child:$child, left:$left, dx:$dx")
//            return super.clampViewPositionHorizontal(child, left, dx)
            val params = child.layoutParams as LayoutParams
            // 两个if主要是为了让View在ViewGroup里面滑动
            if (params == null) {
                if (paddingLeft > left) {
                    return paddingLeft
                }
                if (width - child.width - paddingRight < left) {
                    return width - child.width - paddingRight
                }
            } else {
                if (paddingLeft + params.leftMargin > left) {
                    return paddingLeft + params.leftMargin
                }
                if (width - child.width - paddingRight - params.rightMargin < left) {
                    return width - child.width - paddingRight - params.rightMargin
                }
            }
            return left
        }

        /**
         * 处理竖直方向上的拖动
         *
         * @param child 被拖动到view
         * @param top   移动到达的y轴的距离
         * @param dy    建议的移动的y距离
         */
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            Log.d(TAG, "clampViewPositionVertical child:$child, top:$top, dy$dy")
//            return super.clampViewPositionVertical(child, top, dy)
            val params = child.layoutParams as LayoutParams
            // 两个if主要是为了让View在ViewGroup里面滑动
            if (params == null) {
                if (paddingTop > top) {
                    return paddingTop
                }
                if (height - child.height - paddingBottom < top) {
                    return height - child.height
                }
            } else {
                if (paddingTop + params.topMargin > top) {
                    return paddingTop + params.topMargin
                }
                if (height - child.height - paddingBottom - params.bottomMargin < top) {
                    return height - child.height - paddingBottom - params.bottomMargin
                }
            }
            return top
        }

        /**
         * 手指释放的时候回调
         */
        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            Log.d(TAG, "onViewReleased releasedChild:$releasedChild, xvel:$xvel, yvel$yvel")
            super.onViewReleased(releasedChild, xvel, yvel)
            val params = releasedChild.layoutParams as LayoutParams
            if (params == null) {
                if (leftOrRight) {
                    mDragHelper?.settleCapturedViewAt(paddingLeft, releasedChild.y.toInt())
                } else {
                    mDragHelper?.settleCapturedViewAt(width - releasedChild.width - paddingRight, releasedChild.y.toInt())
                }
            } else {
                if (leftOrRight) {
                    mDragHelper?.settleCapturedViewAt(paddingLeft + params.leftMargin, releasedChild.y.toInt())
                } else {
                    mDragHelper?.settleCapturedViewAt(width - releasedChild.width - paddingRight - params.rightMargin, releasedChild.y.toInt())
                }
            }
            invalidate()
        }


        /**
         * 如果子View不消耗事件，那么整个手势（DOWN-MOVE*-UP）都是直接进入onTouchEvent，在onTouchEvent的DOWN的时候就确定了captureView。
         * 如果消耗事件，那么就会先走onInterceptTouchEvent方法，判断是否可以捕获，
         * 而在判断的过程中会去判断另外两个回调的方法：getViewHorizontalDragRange和getViewVerticalDragRange，只有这两个方法返回大于0的值才能正常的捕获。
         */
        override fun getViewHorizontalDragRange(child: View): Int {
//            return super.getViewHorizontalDragRange(child)
            Log.d(TAG, "getViewHorizontalDragRange child:$child, childView:$childView")
            return measuredWidth - child.measuredWidth
        }

        override fun getViewVerticalDragRange(child: View): Int {
            Log.d(TAG, "getViewVerticalDragRange child:$child, childView:$childView")
//            return super.getViewVerticalDragRange(child)
            return measuredHeight - child.measuredHeight
        }

        /**
         * 当拖拽到状态改变时回调
         *
         * @param state 新的状态
         */
        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            Log.d(TAG, "onViewDragStateChanged state:$state, childView:$childView")
            when (state) {
                ViewDragHelper.STATE_DRAGGING -> {
// 正在被拖动
                }
                ViewDragHelper.STATE_IDLE -> {
// view没有被拖拽或者 正在进行fling/snap
                }
                ViewDragHelper.STATE_SETTLING -> {
// fling完毕后被放置到一个位置
                }
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 1) {
            throw IllegalStateException("DragLayout can host only one direct child")
        }
        childView = getChildAt(0)
        Log.d(TAG, "onFinishInflate childView:$childView")
        childView?.isClickable = true
    }

    override fun computeScroll() {
        super.computeScroll()
        Log.d(TAG, "onFinishInflate childView:$childView")
        if (null != mDragHelper) {
            if (mDragHelper!!.continueSettling(true)) {
                invalidate()
            }
        }
    }

    /**
     * 检查是否可以拦截touch事件
     * 如果onInterceptTouchEvent可以return true 则这里return true
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "onInterceptTouchEvent childView:$childView")
        if (ev != null) {
            mDragHelper?.shouldInterceptTouchEvent(ev)
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent event:$event")
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                if (checkNeedIntercept(event)) {
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                leftOrRight = event.x < width / 2
            }
        }
        /**
         * 处理拦截到的事件
         * 这个方法会在返回前分发事件
         */
        if (event != null) {
            mDragHelper?.processTouchEvent(event)
//            return true
            return false
        }
        return super.onTouchEvent(event)
    }

    /**
     * 如果view不在边上停靠时，触摸view需要拦截，否则会出现无法滑动的情况
     */
    private fun checkNeedIntercept(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent childView:$childView")
        if (null != childView) {
            val params = childView?.layoutParams as LayoutParams
            if (params == null) {
                if (childView!!.x as Int - this.paddingLeft != 0 && childView!!.x as Int + childView!!.width + this.paddingRight != this.width) {
                    mDragHelper?.processTouchEvent(event)
                    return true
                }
            } else {
                if (childView!!.x as Int - this.paddingLeft - params.leftMargin != 0 && childView!!.x as Int + childView!!.width + this.paddingRight + params.rightMargin != this.width) {
                    mDragHelper?.processTouchEvent(event)
                    return true
                }
            }
        }
        return false
    }

}
