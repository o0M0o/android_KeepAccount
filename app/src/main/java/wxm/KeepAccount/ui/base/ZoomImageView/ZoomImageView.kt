package wxm.KeepAccount.ui.base.ZoomImageView

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import wxm.KeepAccount.utility.let1
import wxm.androidutil.log.TagLog
import wxm.androidutil.util.doJudge

/**
 * @author WangXM
 * @version create：2018/6/6
 */
class ZoomImageView
/**
 * 重写 3个 构造方法
 *
 * @param context
 * @param attrs
 * @param defStyleAttr
 */
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : android.support.v7.widget.AppCompatImageView(context, attrs, defStyleAttr),
        ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener,
        View.OnTouchListener {

    /**
     * 自由的放大 和 缩小 放大 可以 自由的 移动 处理 和viewpager 事件冲突
     *
     *
     * 1.Matrix 2.ScaleGestureDetector 3.GestureDetector 4.事件分发机制
     *
     *
     * 1.实现
     *
     */

    // 第一次运行 初始化
    private var mIsOnce = false

    // 缩放比例
    private var mInitScale = 0.toFloat()
    private var mMinScale = 0.toFloat()
    private var mMaxScale = 0.toFloat()

    // 缩放实现
    private val mMatrix = Matrix()

    // 多点触控
    private val mScaleGestureDetector: ScaleGestureDetector

    // 自由移动的比较值
    private val mTouchSlop: Int

    // 双击 放大缩小
    private val mGestureDetector: GestureDetector

    // 判断双击中
    private var mIsDoubleTag = false

    /**
     * 拿到当前图片的缩放值
     *
     * @return
     */
    val scale: Float
        get() {
            val values = FloatArray(9)
            mMatrix.getValues(values)
            return values[Matrix.MSCALE_X]
        }

    // ---------------------------------------------------------自由移动
    // 存储最后的位置
    private var mLastPointCount: Int = 0
    private var mLastX = 0.toFloat()
    private var mLastY = 0.toFloat()
    private var mIsDrag = false
    private var mIsCheckLeftAndRight = false
    private var mIsCheckTopAndBottom = false

    private var mMoveCount = 0

    // ------------------------------------------------比例缩放
    /**
     * 获得 图片放大缩小 以后的宽和高
     *
     * @return
     */
    private val matrixRectF: RectF
        get() {
            val rectF = RectF()
            drawable?.let1 {
                rectF.set(0f, 0f, it.intrinsicWidth.toFloat(),
                        it.intrinsicHeight.toFloat())
                mMatrix.mapRect(rectF)
            }

            return rectF
        }

    init {
        scaleType = ImageView.ScaleType.MATRIX

        // 初始化操作写在 3个参数的 构造函数里
        mScaleGestureDetector = ScaleGestureDetector(context, this)
        setOnTouchListener(this)

        // 初始化 比较值
        mTouchSlop = ViewConfiguration.get(context).scaledDoubleTapSlop

        // 双击放大缩小
        val it = object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                // 双击事件
                if (!mIsDoubleTag) {
                    mIsDoubleTag = true

                    val s = (scale < mMinScale).doJudge(mMinScale, mInitScale)
                    postDelayed(AutoScaleRunnable(s, e.x, e.y), 16)
                }

                return mIsDoubleTag
            }
        }
        mGestureDetector = GestureDetector(context, it)
    }

    /**
     * 自动放大 缩小 缓慢的缩放
     *
     * @author yuan
     */
    private inner class AutoScaleRunnable
    /**
     * 实现构造方法
     *
     * @param mTargetScale
     * @param x
     * @param y
     */
    (private val mTargetScale: Float, private val x: Float, private val y: Float) : Runnable {

        private val BIGGER = 1.07f
        private val SMALL = 0.93f
        private var tmpScale: Float = 0.toFloat()

        init {
            if (scale < mTargetScale) {
                tmpScale = BIGGER
            }
            if (scale > mTargetScale) {
                tmpScale = SMALL
            }
        }

        override fun run() {
            //
            mMatrix.postScale(tmpScale, tmpScale, x, y)
            checkBorderAndCenterWhenScale()
            imageMatrix = mMatrix

            val currentScale = scale
            if (tmpScale > 1.0f && currentScale < mTargetScale || tmpScale < 1.0f && currentScale > mTargetScale) {
                postDelayed(this, 16)
            } else {
                val scale = mTargetScale / currentScale
                mMatrix.postScale(scale, scale, x, y)
                checkBorderAndCenterWhenScale()
                imageMatrix = mMatrix
                mIsDoubleTag = false
            }
        }

    }

    override fun onAttachedToWindow() {
        // 注册 GlobalListener
        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        // 初始化
        val d = drawable
        if (!mIsOnce && null != d) {
            // 控件的宽和高
            val width = width
            val height = height

            // 获得图片的宽和高
            val imgWidth = d.intrinsicWidth
            val imgHeight = d.intrinsicHeight

            // 缩放比例
            var scale = 1.0f
            if (imgWidth > width && imgHeight < height) {
                scale = width * 1.0f / imgWidth
            } else if (imgHeight > height && imgWidth < width) {
                scale = height * 1.0f / imgHeight
            } else if (imgWidth > width && imgHeight > height || imgWidth < width && imgHeight < height) {
                scale = Math.min(width * 1.0f / imgWidth, height * 1.0f / imgHeight)
            }

            /**
             * 初始化 缩放比例
             */
            mInitScale = scale
            mMaxScale = scale * 4
            mMinScale = scale * 2

            /**
             * 将图片移动到 图片中心
             *
             */
            val mx = width / 2 - imgWidth / 2
            val my = height / 2 - imgHeight / 2

            /**
             * Matrix 3*3 矩阵 xScale xskew xTrans yScale yskew yTrans 0 0 0
             * 使用 post 操作
             */
            mMatrix.postTranslate(mx.toFloat(), my.toFloat())
            mMatrix.postScale(mInitScale, mInitScale, (width / 2).toFloat(), (height / 2).toFloat())
            imageMatrix = mMatrix

            mIsOnce = true
        }
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (drawable == null) {
            return true
        }

        // 缩放比例 mInitScale mMaxScale
        var scaleFactor = detector.scaleFactor

        val scale = scale
        if (scale < mMaxScale && scaleFactor > 1.0f || scale > mInitScale && scaleFactor < 1.0f) {
            if (scale * scaleFactor < mInitScale) {
                scaleFactor = mInitScale / scale
            } else if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale
            }

            mMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
            checkBorderAndCenterWhenScale()
            imageMatrix = mMatrix
        }

        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // 双击的时候不让其 移动
        if (mGestureDetector.onTouchEvent(event)) {
            return true
        }

        // 设置 onTouch 事件
        mScaleGestureDetector.onTouchEvent(event)

        // 自由移动实现
        var x = 0f
        var y = 0f
        val pointerCount = event.pointerCount
        for (i in 0 until pointerCount) {
            x += event.getX(i)
            y += event.getY(i)
        }
        x /= pointerCount.toFloat()
        y /= pointerCount.toFloat()

        if (mLastPointCount != pointerCount) {
            mIsDrag = false
            mLastX = x
            mLastY = y

        }
        mLastPointCount = pointerCount

        // 处理事件冲突问题！！
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mMoveCount = 0
                TagLog.i("ACTION_DOWN, mLastX=$mLastX, mLastY=$mLastY, x=$x, y=$y")
                // 解决事件冲突
                // 当图片的 高度和宽度 大于屏幕的寬高度的时候，为图片的事件，否则为viewPager
                if (isChong(matrixRectF)) {
                    if (parent is ViewPager) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                mMoveCount += 1
                TagLog.i("ACTION_MOVE, mMoveCount=$mMoveCount mLastX=$mLastX, mLastY=$mLastY, x=$x, y=$y")
                val f = matrixRectF
                // 解决事件冲突
                if (isChong(f)) {
                    if (parent is ViewPager) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

                // 正在移动
                var dx = x - mLastX
                var dy = y - mLastY
                if (!mIsDrag) {
                    mIsDrag = isMoveAction(dx, dy)
                }

                if (mIsDrag && mMoveCount > 1) {
                    drawable?.let1 {
                        mIsCheckTopAndBottom = true
                        mIsCheckLeftAndRight = mIsCheckTopAndBottom

                        // 如果高度 小于控件宽度，不允许横向移动
                        if (f.width() < width) {
                            mIsCheckLeftAndRight = false
                            dx = 0f
                        }

                        if (f.height() < height) {
                            mIsCheckTopAndBottom = false
                            dy = 0f
                        }

                        mMatrix.postTranslate(dx, dy)
                        checkBorderWhenTranslate()
                        imageMatrix = mMatrix
                        isFocusable = true
                    }
                }

                mLastY = y
                mLastX = x
            }

            MotionEvent.ACTION_CANCEL -> {
                mLastPointCount = 0
            }
        }

        return true
    }

    /**
     * 解决 事件冲突
     *
     * @param f
     * @return
     */
    private fun isChong(f: RectF): Boolean {
        return f.width() > width + 0.01 || f.height() > height + 0.01
    }

    /**
     * 移动 判断边界
     */

    private fun checkBorderWhenTranslate() {
        val rectf = matrixRectF
        var dx = 0f
        var dy = 0f
        val width = width
        val height = height

        if (rectf.top > 0 && mIsCheckTopAndBottom) {
            dy = -rectf.top
        }
        if (rectf.bottom < height && mIsCheckTopAndBottom) {
            dy = height - rectf.bottom
        }

        if (rectf.right < width && mIsCheckLeftAndRight) {
            dx = width - rectf.right
        }
        if (rectf.left > 0 && mIsCheckLeftAndRight) {
            dx = -rectf.left
        }
        mMatrix.postTranslate(dx, dy)
    }

    /**
     * 判断是否移动
     *
     * @param dx
     * @param dy
     * @return
     */
    private fun isMoveAction(dx: Float, dy: Float): Boolean {
        return Math.sqrt((dx * dx + dy * dy).toDouble()) > mTouchSlop
    }

    /**
     * 防止缩放时，出现白边
     */
    private fun checkBorderAndCenterWhenScale() {
        val rectf = matrixRectF
        var dx = 0f
        var dy = 0f
        val width = width
        val height = height
        // 防止出现 白边
        if (rectf.width() >= width) {
            if (rectf.left > 0) {
                dx = -rectf.left
            }
            if (rectf.right < width) {
                dx = width - rectf.right
            }

        }
        if (rectf.height() >= height) {
            if (rectf.top > 0) {
                dy = -rectf.top
            }
            if (rectf.bottom < height) {
                dy = height - rectf.bottom
            }
        }

        // 如果 宽度和高度 小于 控件宽度和高度，则让其 居中；
        if (rectf.width() < width) {
            dx = width / 2 - rectf.right + rectf.width() / 2
        }

        if (rectf.height() < height) {
            dy = height / 2 - rectf.bottom + rectf.height() / 2
        }

        mMatrix.postTranslate(dx, dy)
    }
}
