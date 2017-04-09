package wxm.KeepAccount.ui.extend.ValueShow;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;


/**
 *  显示数据图示的扩展视图
 *
 * @author      wxm
 * @version create：2017/03/28
 */
public class ValueShow extends ConstraintLayout {
    private final static String LOG_TAG = "ValueShow";

    @BindView(R.id.tv_pay_info)
    TextView    mTVPayInfo;

    @BindView(R.id.tv_income_info)
    TextView    mTVIncomeInfo;

    @BindView(R.id.iv_pay_line)
    ImageView   mIVPayLine;

    @BindView(R.id.iv_income_line)
    ImageView   mIVIncomeLine;

    /**
     * 可设置属性
     */
    private String mAttrTextOn;
    private String mAttrTextOff;

    private Drawable     mAttrBackGroundOn;
    private Drawable     mAttrBackGroundOff;

    private boolean mAttrIsOn;

    /**
     * 固定变量
     */

    public ValueShow(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.vw_value_show, this);
        ButterKnife.bind(this);
        initCompent(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * 初始化自身
     * @param context   上下文
     * @param attrs     配置
     */
    private void initCompent(Context context, AttributeSet attrs)  {
        // for parameter
        boolean b_ok = true;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SmallButton);
        try {
            mAttrTextOn = array.getString(R.styleable.SmallButton_sbTextOn);
            mAttrTextOn = UtilFun.StringIsNullOrEmpty(mAttrTextOn) ? "on" : mAttrTextOn;

            mAttrTextOff = array.getString(R.styleable.SmallButton_sbTextOff);
            mAttrTextOff = UtilFun.StringIsNullOrEmpty(mAttrTextOff) ? "off" : mAttrTextOff;

            mAttrIsOn = array.getBoolean(R.styleable.SmallButton_sbIsOn, false);

            mAttrBackGroundOn = array.getDrawable(R.styleable.SmallButton_sbBackGroundOn);
            mAttrBackGroundOff = array.getDrawable(R.styleable.SmallButton_sbBackGroundOff);
        } catch (Exception ex)  {
            b_ok = false;
            Log.e(LOG_TAG, "catch ex : " + ex.toString());
        } finally {
            array.recycle();
        }

        if(b_ok) {
        }
    }
}
