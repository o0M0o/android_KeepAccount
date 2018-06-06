package wxm.KeepAccount.ui.base.ZoomImageView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewTreeObserver

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
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : android.support.v7.widget.AppCompatImageView(context, attrs, defStyleAttr), ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

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
    private var isOnce = false
    // 缩放比例
    private var initScale: Float = 0.toFloat()
    private var minScale: Float = 0.toFloat()
    private var maxScale: Float = 0.toFloat()

    // 缩放实现
    private val matrix: Matrix

    // 多点触控
    private val scaleGestureDetector: ScaleGestureDetector

    // 自由移动的比较值
    private val touchSlop: Int

    // 双击 放大缩小
    private val gestureDetector: GestureDetector

    // 判断双击中
    private var isDoubletag = false

    /**
     * 拿到当前图片的缩放值
     *
     * @return
     */
    val scale: Float
        get() {
            val values = FloatArray(9)
            matrix.getValues(values)
            return values[Matrix.MSCALE_X]
        }

    // ---------------------------------------------------------自由移动
    // 存储最后的位置
    private var lastPointCount: Int = 0
    private var Lx: Float = 0.toFloat()
    private var Ly: Float = 0.toFloat()
    private var isDrag: Boolean = false
    private var isCheckLeftAndRight: Boolean = false
    private var isCheckTopAndBottom = false

    // ------------------------------------------------比例缩放
    /**
     * 获得 图片放大缩小 以后的宽和高
     *
     * @return
     */
    private val matrixRectF: RectF
        get() {
            val smatrix = matrix
            val rectF = RectF()
            val drawable = drawable

            if (drawable != null) {
                rectF.set(0f, 0f, drawable.intrinsicWidth.toFloat(),
                        drawable.intrinsicHeight.toFloat())
                smatrix.mapRect(rectF)
            }
            return rectF
        }

    init {

        //
        matrix = Matrix()
        scaleType = ImageView.ScaleType.MATRIX

        // 初始化操作写在 3个参数的 构造函数里

        scaleGestureDetector = ScaleGestureDetector(context, this)
        setOnTouchListener(this)

        // 初始化 比较值
        touchSlop = ViewConfiguration.get(context).scaledDoubleTapSlop

        // 双击放大缩小
        gestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDoubleTap(e: MotionEvent): Boolean {
                        // 双击事件
                        if (isDoubletag) {
                            return isDoubletag
                        }
                        val x = e.x
                        val y = e.y
                        if (scale < minScale) {

                            postDelayed(AutoScaleRunnable(minScale, x, y),
                                    16)
                            isDoubletag = true
                        } else {
                            postDelayed(AutoScaleRunnable(initScale, x, y),
                                    16)
                            isDoubletag = true
                        }

                        return true
                    }
                })
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
            matrix.postScale(tmpScale, tmpScale, x, y)
            checkBorderAndCenterWhenScale()
            imageMatrix = matrix

            val currentScale = scale
            if (tmpScale > 1.0f && currentScale < mTargetScale || tmpScale < 1.0f && currentScale > mTargetScale) {
                postDelayed(this, 16)
            } else {
                val scale = mTargetScale / currentScale
                matrix.postScale(scale, scale, x, y)
                checkBorderAndCenterWhenScale()
                imageMatrix = matrix
                isDoubletag = false
            }
        }

    }

    override fun onAttachedToWindow() {
        // 注册 GlobalListener

        super.onAttachedToWindow()
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    @SuppressLint("NewApi")
    override fun onDetachedFromWindow() {
        // 移除 GlobalListener
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        // 初始化
        if (!isOnce) {
            // 控件的宽和高
            val width = width
            val height = height
            // 获得图片的宽和高
            val d = drawable ?: return

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
            initScale = scale
            maxScale = scale * 4
            minScale = scale * 2

            Log.i("TAG", scale.toString() + "")
            /**
             * 将图片移动到 图片中心
             *
             */
            val mx = width / 2 - imgWidth / 2
            val my = height / 2 - imgHeight / 2

            /**
             * Matrix 3*3 矩阵 xScale xskew xTrans yScale yskew yTrans 0 0 0
             *
             * 使用 post 操作
             */
            matrix.postTranslate(mx.toFloat(), my.toFloat())
            matrix.postScale(initScale, initScale, (width / 2).toFloat(), (height / 2).toFloat())
            imageMatrix = matrix

            isOnce = true
        }

    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        // 缩放比例 initScale maxScale
        var scaleFactor = detector.scaleFactor

        if (drawable == null) {
            return true
        }
        var scale = scale
        // 缩放控制
        if (scale < maxScale && scaleFactor > 1.0f || scale > initScale && scaleFactor < 1.0f) {
            if (scale * scaleFactor < initScale) {
                scaleFactor = initScale / scale
            } else if (scale * scaleFactor > maxScale) {
                scale = maxScale / scale
            }
            matrix.postScale(scaleFactor, scaleFactor, detector.focusX,
                    detector.focusY)

            checkBorderAndCenterWhenScale()
            imageMatrix = matrix
        }

        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        // 返回true
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        // TODO Auto-generated method stub

    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        // 双击的时候不让其 移动
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }

        // 设置 onTouch 事件
        scaleGestureDetector.onTouchEvent(event)

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

        if (lastPointCount != pointerCount) {
            isDrag = false
            Lx = x
            Ly = y

        }
        lastPointCount = pointerCount

        val f = matrixRectF

        // 处理事件冲突问题！！
        when (event.action) {

            MotionEvent.ACTION_DOWN ->
                // 解决事件冲突
                // 当图片的 高度和宽度 大于屏幕的寬高度的时候，为图片的事件，否则为viewPager
                if (ischong(f)) {
                    if (parent is ViewPager) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

            MotionEvent.ACTION_MOVE -> {
                // 解决事件冲突
                if (ischong(f)) {
                    if (parent is ViewPager) {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }

                // 正在移动
                var dx = x - Lx
                var dy = y - Ly
                if (!isDrag) {
                    isDrag = isMoveAction(dx, dy)
                }
                if (isDrag) {

                    if (drawable != null) {
                        isCheckTopAndBottom = true
                        isCheckLeftAndRight = isCheckTopAndBottom
                        // 如果高度 小于控件宽度，不允许横向移动
                        if (f.width() < width) {
                            isCheckLeftAndRight = false
                            dx = 0f
                        }
                        if (f.height() < height) {
                            isCheckTopAndBottom = false
                            dy = 0f
                        }
                        matrix.postTranslate(dx, dy)
                        checkBorderWhenTranslate()
                        imageMatrix = matrix
                        isFocusable = true
                    }
                }
                Ly = y
                Lx = x
            }

            MotionEvent.ACTION_CANCEL -> {
                Log.i("TAG", "onTouch" + 4)
                lastPointCount = 0
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
    private fun ischong(f: RectF): Boolean {
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

        if (rectf.top > 0 && isCheckTopAndBottom) {
            dy = -rectf.top
        }
        if (rectf.bottom < height && isCheckTopAndBottom) {
            dy = height - rectf.bottom
        }

        if (rectf.right < width && isCheckLeftAndRight) {
            dx = width - rectf.right
        }
        if (rectf.left > 0 && isCheckLeftAndRight) {
            dx = -rectf.left
        }
        matrix.postTranslate(dx, dy)
    }

    /**
     * 判断是否移动
     *
     * @param dx
     * @param dy
     * @return
     */
    private fun isMoveAction(dx: Float, dy: Float): Boolean {

        return Math.sqrt((dx * dx + dy * dy).toDouble()) > touchSlop
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

        matrix.postTranslate(dx, dy)
    }

}
