package wxm.KeepAccount.ui.DataBase;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.wxm.andriodutillib.util.UtilFun;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;
import wxm.KeepAccount.Base.utility.PreferencesUtil;
import wxm.KeepAccount.R;

/**
 * for frg
 * Created by wangxm on 16/12/15.
 */
public class FrgAdapter implements LBaseAdapter<FrgPara> {
    private final static String    LOG_TAG = "FrgAdapter";

    private Context             mContext;
    private SparseArray<View>   mSAView;
    private Timestamp           mTSLastUpdate;

    public FrgAdapter(Context context) {
        mContext=context;
        mSAView = new SparseArray<>();

        initView();
    }

    @Override
    public View getView(final LMBanners lBanners, final Context context, int position, FrgPara data) {
        /*
        Timestamp ts_data = ContextUtil.getPayIncomeUtility().getDataLastChangeTime();
        Log.d(LOG_TAG, "getView, pos = " + position + ", data = " + data.toString());
        Log.d(LOG_TAG, "ts_db = " + ts_data.toString() + ", ts_view = " + mTSLastUpdate.toString());

        if(mTSLastUpdate.before(ts_data))   {
            initView();
        }

        return mSAView.get(data.mFPViewId);
        */

        View v = null;
        switch (data.mFPViewId) {
            case R.layout.banner_month : {
                v = LayoutInflater.from(mContext).inflate(R.layout.banner_month, null);
                fillMonth(v);
            }
            break;

            case R.layout.banner_year : {
                v = LayoutInflater.from(mContext).inflate(R.layout.banner_year, null);
                fillYear(v);
            }
            break;
        }

        return v;
    }


    private void initView() {
        Log.d(LOG_TAG, "initView");

        mSAView.clear();
        mTSLastUpdate = new Timestamp(System.currentTimeMillis());

        View v = LayoutInflater.from(mContext).inflate(R.layout.banner_month, null);
        fillMonth(v);
        mSAView.put(R.layout.banner_month, v);

        v = LayoutInflater.from(mContext).inflate(R.layout.banner_year, null);
        fillYear(v);
        mSAView.put(R.layout.banner_year, v);
    }

    /**
     * 填充月数据
     * @param v   视图
     */
    private void fillMonth(View v)  {
        TextView tv_pay = UtilFun.cast_t(v.findViewById(R.id.tv_pay_amount));
        TextView tv_income = UtilFun.cast_t(v.findViewById(R.id.tv_income_amount));

        TextView tv_month = UtilFun.cast_t(v.findViewById(R.id.tv_month_number));
        TextView tv_year = UtilFun.cast_t(v.findViewById(R.id.tv_year_number));

        Calendar ci = Calendar.getInstance();
        tv_year.setText(String.format(Locale.CHINA, "%04d", ci.get(Calendar.YEAR)));
        tv_month.setText(String.format(Locale.CHINA, "%02d", ci.get(Calendar.MONTH) + 1));

        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM", Locale.CHINA);
        String cur_month = sd.format(ci.getTime());
        HashMap<String, NoteShowInfo> hm = NoteShowDataHelper.getInstance().getMonthInfo();
        NoteShowInfo ni = hm.get(cur_month);
        if(null == ni)  {
            ni = new NoteShowInfo();
            ni.setPayAmount(BigDecimal.ZERO);
            ni.setIncomeAmount(BigDecimal.ZERO);
        }

        tv_pay.setText(String.format(Locale.CHINA, "%.02f", ni.getPayAmount().floatValue()));
        tv_income.setText(String.format(Locale.CHINA, "%.02f", ni.getIncomeAmount().floatValue()));
        fillChart(v, ni);
    }

    /**
     * 填充年数据
     * @param v   视图
     */
    private void fillYear(View v)  {
        TextView tv_pay = UtilFun.cast_t(v.findViewById(R.id.tv_pay_amount));
        TextView tv_income = UtilFun.cast_t(v.findViewById(R.id.tv_income_amount));

        TextView tv_year = UtilFun.cast_t(v.findViewById(R.id.tv_year_number));

        Calendar ci = Calendar.getInstance();
        tv_year.setText(String.format(Locale.CHINA, "%04d", ci.get(Calendar.YEAR)));

        SimpleDateFormat sd = new SimpleDateFormat("yyyy", Locale.CHINA);
        String cur_year = sd.format(ci.getTime());
        HashMap<String, NoteShowInfo> hm = NoteShowDataHelper.getInstance().getYearInfo();
        NoteShowInfo ni = hm.get(cur_year);
        if(null == ni)  {
            ni = new NoteShowInfo();
            ni.setPayAmount(BigDecimal.ZERO);
            ni.setIncomeAmount(BigDecimal.ZERO);
        }

        tv_pay.setText(String.format(Locale.CHINA, "%.02f", ni.getPayAmount().floatValue()));
        tv_income.setText(String.format(Locale.CHINA, "%.02f", ni.getIncomeAmount().floatValue()));
        fillChart(v, ni);
    }

    private void fillChart(View v, NoteShowInfo ni) {
        // 展示条
        HashMap<String, Integer> mHMColor = PreferencesUtil.loadChartColor();
        ImageView iv = UtilFun.cast_t(v.findViewById(R.id.iv_income));
        iv.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_INCOME_COLOR));

        iv = UtilFun.cast_t(v.findViewById(R.id.iv_pay));
        iv.setBackgroundColor(mHMColor.get(PreferencesUtil.SET_PAY_COLOR));

        // for chart
        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values = new ArrayList<>();
        values.add(new SubcolumnValue(ni.getPayAmount().floatValue(),
                mHMColor.get(PreferencesUtil.SET_PAY_COLOR)));
        values.add(new SubcolumnValue(ni.getIncomeAmount().floatValue(),
                mHMColor.get(PreferencesUtil.SET_INCOME_COLOR)));

        Column cd = new Column(values);
        cd.setHasLabels(false);
        columns.add(cd);

        axisValues.add(new AxisValue(0).setLabel(""));

        ColumnChartData mChartData = new ColumnChartData(columns);
        mChartData.setAxisXBottom(new Axis(axisValues));
        mChartData.setAxisYLeft(new Axis().setHasLines(true));

        ColumnChartView mChart = UtilFun.cast_t(v.findViewById(R.id.chart));
        mChart.setColumnChartData(mChartData);
    }
}
