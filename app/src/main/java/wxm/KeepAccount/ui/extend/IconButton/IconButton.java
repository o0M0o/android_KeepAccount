package wxm.KeepAccount.ui.extend.IconButton;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
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

    @BindView(R.id.tv_tag)
    TextView    mTVName;

    @BindView(R.id.iv_tag)
    ImageView   mIVIcon;


    /**
     * 可设置属性
     */
    // 动作名
    private String  mAttrActName;

    // 动作名尺寸
    private float   mAttrActNameSize;

    // 动作名颜色
    private int     mAttrActNameColor;

    //动作icon的资源id
    private int     mAttrActIconID;

    // 动作icon的宽和高
    private int     mAttrActIconWidth;
    private int     mAttrActIconHeight;


    public IconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.vw_icon_button, this);
        ButterKnife.bind(this);
        initCompent(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
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
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IconButton);
        try {
            // for icon
            mAttrActIconWidth = array.getDimensionPixelSize(R.styleable.IconButton_dpIconWidth, 36);
            mAttrActIconHeight = array.getDimensionPixelSize(R.styleable.IconButton_dpIconHeight, 36);

            mAttrActIconID = array.getResourceId(R.styleable.IconButton_rfIcon, R.drawable.ic_look);

            // for name
            int def_color = context.getResources().getColor(R.color.text_fit);
            mAttrActNameSize = array.getDimensionPixelSize(R.styleable.IconButton_spActNameSize, 14);
            mAttrActNameColor = array.getColor(R.styleable.IconButton_crActNameColor, def_color);

            mAttrActName = array.getString(R.styleable.IconButton_szActName);
            mAttrActName = UtilFun.StringIsNullOrEmpty(mAttrActName) ? "action" : mAttrActName;
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
        // for icon
        ViewGroup.LayoutParams lp = mIVIcon.getLayoutParams();
        lp.width = mAttrActIconWidth;
        lp.height = mAttrActIconHeight;
        mIVIcon.setLayoutParams(lp);

        mIVIcon.setImageResource(mAttrActIconID);

        // for name
        mTVName.setText(mAttrActName);
        mTVName.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrActNameSize);
        mTVName.setTextColor(mAttrActNameColor);

        invalidate();
        requestLayout();
    }
}
