package wxm.KeepAccount.ui.data.show.note.base;


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
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;


/**
 * use this help show data
 * @author      wxm
 * @version     create：2017/03/28
 */
public class ValueShow extends ConstraintLayout {
    public final static String ATTR_PAY_COUNT = "pay_count";
    public final static String ATTR_PAY_AMOUNT = "pay_amount";
    public final static String ATTR_INCOME_COUNT = "income_count";
    public final static String ATTR_INCOME_AMOUNT = "income_amount";
    private final static String LOG_TAG = "ValueShow";
    @BindView(R.id.tv_pay_tag)
    TextView mTVPayTag;

    @BindView(R.id.tv_pay_count)
    TextView mTVPayCount;

    @BindView(R.id.tv_pay_amount)
    TextView mTVPayAmount;

    @BindView(R.id.tv_income_tag)
    TextView mTVIncomeTag;

    @BindView(R.id.tv_income_count)
    TextView mTVIncomeCount;

    @BindView(R.id.tv_income_amount)
    TextView mTVIncomeAmount;

    @BindView(R.id.tv_balance_amount)
    TextView mTVBalanceAmount;

    @BindView(R.id.iv_pay_line)
    ImageView mIVPayLine;

    @BindView(R.id.iv_income_line)
    ImageView mIVIncomeLine;

    /**
     * can set attributes
     */
    private String mAttrPayCount;
    private String mAttrPayAmount;

    private String mAttrIncomeCount;
    private String mAttrIncomeAmount;

    private int mAttrDMLineLen;

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
     * adjust attributes
     * @param attrs     new attributes
     */
    public void adjustAttribute(Map<String, Object> attrs) {
        for (String k : attrs.keySet()) {
            switch (k) {
                case ATTR_PAY_COUNT:
                    mAttrPayCount = (String) attrs.get(k);
                    break;

                case ATTR_PAY_AMOUNT:
                    mAttrPayAmount = (String) attrs.get(k);
                    break;

                case ATTR_INCOME_COUNT:
                    mAttrIncomeCount = (String) attrs.get(k);
                    break;

                case ATTR_INCOME_AMOUNT:
                    mAttrIncomeAmount = (String) attrs.get(k);
                    break;
            }
        }

        updateShow();
    }

    /**
     * use date init UI
     * @param context   context for UI
     * @param attrs     data for UI
     */
    private void initCompent(Context context, AttributeSet attrs) {
        // for parameter
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ValueShow);
        try {
            mAttrPayCount = array.getString(R.styleable.ValueShow_szPayCount);
            mAttrPayCount = UtilFun.StringIsNullOrEmpty(mAttrPayCount) ? "0" : mAttrPayCount;

            mAttrPayAmount = array.getString(R.styleable.ValueShow_szPayAmount);
            mAttrPayAmount = UtilFun.StringIsNullOrEmpty(mAttrPayAmount) ? "0" : mAttrPayAmount;

            mAttrIncomeCount = array.getString(R.styleable.ValueShow_szIncomeCount);
            mAttrIncomeCount = UtilFun.StringIsNullOrEmpty(mAttrIncomeCount) ? "0" : mAttrIncomeCount;

            mAttrIncomeAmount = array.getString(R.styleable.ValueShow_szIncomeAmount);
            mAttrIncomeAmount = UtilFun.StringIsNullOrEmpty(mAttrIncomeAmount) ? "0" : mAttrIncomeAmount;

            float dst = getContext().getResources().getDisplayMetrics().density;
            mAttrDMLineLen = array.getLayoutDimension(R.styleable.ValueShow_pxLineLen,
                    (int) (200 * dst));

            updateShow();
        } catch (Exception ex) {
            Log.e(LOG_TAG, "catch ex : " + ex.toString());
        } finally {
            array.recycle();
        }
    }

    /**
     * update UI
     */
    private void updateShow() {
        mTVPayCount.setText(mAttrPayCount);
        mTVPayAmount.setText(mAttrPayAmount);

        mTVIncomeCount.setText(mAttrIncomeCount);
        mTVIncomeAmount.setText(mAttrIncomeAmount);

        BigDecimal bd_pay = new BigDecimal(mAttrPayAmount);
        BigDecimal bd_income = new BigDecimal(mAttrIncomeAmount);
        BigDecimal bd_big = new BigDecimal(Math.max(bd_pay.floatValue(), bd_income.floatValue()));

        BigDecimal bd_balance = bd_income.subtract(bd_pay);
        String sz_b = String.format(Locale.CHINA, "%.02f", bd_balance.floatValue());
        mTVBalanceAmount.setText(sz_b);

        boolean b_p = mAttrPayCount.equals("0");
        mIVPayLine.setVisibility(b_p ? View.GONE : View.VISIBLE);
        mTVPayTag.setVisibility(b_p ? View.GONE : View.VISIBLE);
        mTVPayCount.setVisibility(b_p ? View.GONE : View.VISIBLE);
        mTVPayAmount.setVisibility(b_p ? View.GONE : View.VISIBLE);
        float mPayLinePercent = b_p ? 0 : bd_pay.floatValue() / bd_big.floatValue();

        if (0.01 < mPayLinePercent)
            adjustLineLen(mIVPayLine, mPayLinePercent);

        boolean b_i = mAttrIncomeCount.equals("0");
        mIVIncomeLine.setVisibility(b_i ? View.GONE : View.VISIBLE);
        mTVIncomeTag.setVisibility(b_i ? View.GONE : View.VISIBLE);
        mTVIncomeCount.setVisibility(b_i ? View.GONE : View.VISIBLE);
        mTVIncomeAmount.setVisibility(b_i ? View.GONE : View.VISIBLE);
        float mIncomeLinePercent = b_i ? 0 : bd_income.floatValue() / bd_big.floatValue();

        if (0.01 < mIncomeLinePercent)
            adjustLineLen(mIVIncomeLine, mIncomeLinePercent);

        invalidate();
        requestLayout();
    }

    /**
     * adjust line length
     * @param iv        line need adjust
     * @param percent   new length(percentage)
     */
    private void adjustLineLen(ImageView iv, float percent) {
        ViewGroup.LayoutParams lp = iv.getLayoutParams();
        lp.width = (int) (mAttrDMLineLen * percent);
        iv.setLayoutParams(lp);
    }
}
