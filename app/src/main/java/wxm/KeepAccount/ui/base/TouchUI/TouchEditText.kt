package wxm.KeepAccount.ui.base.TouchUI

import android.annotation.SuppressLint
import android.content.Context
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet

/**
 * use this to suppress warning caused by setOnTouchListener
 * Created by WangXM on 2018/3/24.
 */
class TouchEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        // do what you want
        return true
    }
}
