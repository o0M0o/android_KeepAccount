package wxm.KeepAccount.ui.base.TouchUI

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet

import lecho.lib.hellocharts.view.PreviewColumnChartView

/**
 * @author WangXM
 * @version create：2018/4/9
 */
class TouchPreviewColumnChartView : PreviewColumnChartView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    @SuppressLint("ClickableViewAccessibility")
    override fun performClick(): Boolean {
        // do what you want
        return true
    }
}
