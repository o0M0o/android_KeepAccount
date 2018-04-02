package wxm.KeepAccount.ui.base.TouchUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.util.AttributeSet;

/**
 * use this to suppress warning caused by setOnTouchListener
 * Created by WangXM on 2018/3/24.
 */
public class TouchEditText extends TextInputEditText {
    public TouchEditText(Context context) {
        super(context);
    }

    public TouchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }
}
