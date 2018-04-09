package wxm.KeepAccount.ui.base.TouchUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * @author WangXM
 * @version create：2018/4/9
 */
public class TouchColumnChartView extends ColumnChartView {

    public TouchColumnChartView(Context context) {
        super(context);
    }

    public TouchColumnChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchColumnChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }
}
