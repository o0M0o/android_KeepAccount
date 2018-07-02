package wxm.KeepAccount.ui.base.radioButtonEx

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.RadioButton
import wxm.KeepAccount.R
import wxm.androidutil.improve.let1
import wxm.androidutil.log.TagLog

/**
 * Created by WangXM on 2018/3/24.
 */
class RadioButtonEx : RadioButton {
    private var mDrawWidth = 0
    private var mDrawHeight = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (null != attrs) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.RadioButtonEx)
            try {
                mDrawWidth = array.getDimensionPixelSize(R.styleable.RadioButtonEx_rbt_draw_width, 0)
                mDrawHeight = array.getDimensionPixelSize(R.styleable.RadioButtonEx_rbt_draw_height, 0)

                TagLog.i("width = $mDrawWidth, height = $mDrawHeight")
                if (0 != mDrawHeight && 0 != mDrawWidth) {
                    compoundDrawables?.let1 {
                        it[0]?.let1 { adjustDraw(it, POS_START) }
                        it[1]?.let1 { adjustDraw(it, POS_TOP) }
                        it[2]?.let1 { adjustDraw(it, POS_END) }
                        it[3]?.let1 { adjustDraw(it, POS_BOTTOM) }
                    }

                    compoundDrawablesRelative?.let1 {
                        it[0]?.let1 { adjustDraw(it, POS_START) }
                        it[1]?.let1 { adjustDraw(it, POS_TOP) }
                        it[2]?.let1 { adjustDraw(it, POS_END) }
                        it[3]?.let1 { adjustDraw(it, POS_BOTTOM) }
                    }
                }
            } finally {
                array.recycle()
            }
        }
    }

    private fun adjustDraw(draw: Drawable, pos: Int) {
        val oldBound = draw.bounds
        if(null != oldBound)    {
            val wPad = (oldBound.width() - mDrawWidth) / 2
            val hPad = (oldBound.height() - mDrawHeight) / 2

            oldBound.left += wPad
            oldBound.top += hPad
            oldBound.right = oldBound.left + mDrawWidth
            oldBound.bottom = oldBound.top + mDrawHeight
            draw.bounds = oldBound

            when(pos)   {
                POS_START ->    {
                    compoundDrawablePadding -= wPad
                    adjustPadding(-wPad, 0, 0, 0)
                }

                POS_TOP -> {
                    compoundDrawablePadding -= hPad
                    adjustPadding(0, -hPad, 0, 0)
                }

                POS_END -> {
                    compoundDrawablePadding -= wPad
                    adjustPadding(0, 0, -wPad, 0)
                }

                POS_BOTTOM -> {
                    compoundDrawablePadding -= hPad
                    adjustPadding(0, 0, 0, -hPad)
                }
            }
        }
    }

    private fun adjustPadding(difStart:Int, difTop:Int, difEnd:Int, difBottom:Int)  {
        setPaddingRelative(paddingStart + difStart, paddingTop + difTop,
                paddingEnd + difEnd, paddingBottom + difBottom)
    }

    companion object {
        private const val POS_START = 1
        private const val POS_TOP = 2
        private const val POS_END = 3
        private const val POS_BOTTOM = 4
    }
}
