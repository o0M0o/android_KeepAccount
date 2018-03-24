package wxm.KeepAccount.ui.base;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

/**
 * use this to suppress warning caused by setOnTouchListener
 * Created by ookoo on 2018/3/24.
 */
public class TouchTextView extends android.support.v7.widget.AppCompatTextView {

    public TouchTextView(Context context) {
        super(context);
    }

    public TouchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }
}
