package wxm.KeepAccount.ui.extend.IconButton;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;


/**
 *  button with icon
 *
 * @author      wxm
 * @version create：2017/03/28
 */
public class IconButton extends ConstraintLayout {
    private final static String LOG_TAG = "IconButton";

    @BindView(R.id.tv_pay_count)
    TextView    mTVPayCount;

    @BindView(R.id.tv_pay_amount)
    TextView    mTVPayAmount;

    @BindView(R.id.tv_income_count)
    TextView    mTVIncomeCount;

    @BindView(R.id.tv_income_amount)
    TextView    mTVIncomeAmount;

    @BindView(R.id.iv_pay_line)
    ImageView   mIVPayLine;

    @BindView(R.id.iv_income_line)
    ImageView   mIVIncomeLine;

    /**
     * 可设置属性
     */
    private String mAttrActName;


    // for layout
    private int mWidth;
    private int mHeight;

    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.vw_value_show, this);
        ButterKnife.bind(this);
        initCompent(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mWidth = getWidth();
        mHeight = getHeight();
        super.onLayout(changed, left, top, right, bottom);
    }



    /**
     * 初始化自身
     * @param context   上下文
     * @param attrs     配置
     */
    private void initCompent(Context context, AttributeSet attrs)  {
        // for parameter
        boolean b_ok = true;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ValueShow);
        try {
        } catch (Exception ex)  {
            b_ok = false;
            Log.e(LOG_TAG, "catch ex : " + ex.toString());
        } finally {
            array.recycle();
        }

        if(b_ok) {
            updateShow();
        }
    }

    /**
     * 更新显示
     */
    private void updateShow()   {
        invalidate();
        requestLayout();
    }
}
