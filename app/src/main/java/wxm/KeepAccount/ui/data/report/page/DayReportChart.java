package wxm.KeepAccount.ui.data.report.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.data.report.EventSelectDays;
import wxm.KeepAccount.ui.utility.NoteDataHelper;

/**
 * 日数据汇报 - webview展示页
 * Created by ookoo on 2017/3/4.
 */
public class DayReportChart extends FrgUtilityBase {
    @BindView(R.id.chart)
    PieChartView mCVchart;
    @BindView(R.id.pb_load_data)
    ProgressBar mPBLoadData;
    @BindView(R.id.tb_income)
    ToggleButton mTBIncome;
    @BindView(R.id.tb_pay)
    ToggleButton mTBPay;
    private ArrayList<String> mASParaLoad;
    private LinkedList<INote> mLLOrgData;

    @Override
    protected void enterActivity() {
        Log.d(LOG_TAG, "in enterActivity");
        super.enterActivity();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void leaveActivity() {
        Log.d(LOG_TAG, "in leaveActivity");
        EventBus.getDefault().unregister(this);

        super.leaveActivity();
    }

    /**
     * 更新日期范围
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectDaysEvent(EventSelectDays event) {
        mASParaLoad.set(0, event.mSZStartDay);
        mASParaLoad.set(1, event.mSZEndDay);
        loadData();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "DayReportChart";
        View rootView = layoutInflater.inflate(R.layout.page_report_chart, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        mCVchart.setOnValueTouchListener(new PieChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int i, SliceValue sliceValue) {
                String sz = String.format(Locale.CHINA,
                        "%s : %.02f",
                        String.valueOf(sliceValue.getLabelAsChars()), sliceValue.getValue());

                Toast.makeText(getActivity(), sz, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
            }
        });

        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);
        loadData();
    }

    @Override
    protected void loadUI() {
    }


    /**
     * 切换显示内容
     *
     * @param v 激活的ToggleButton
     */
    @OnClick({R.id.tb_income, R.id.tb_pay})
    public void onTBClick(View v) {
        Log.d(LOG_TAG, "in onSWClick");

        int vid = v.getId();
        switch (vid) {
            case R.id.tb_income: {
                if (mTBIncome.isChecked()) {
                    mTBPay.setClickable(true);
                } else {
                    mTBPay.setClickable(false);
                }
            }
            break;

            case R.id.tb_pay: {
                if (mTBPay.isChecked()) {
                    mTBIncome.setClickable(true);
                } else {
                    mTBIncome.setClickable(false);
                }
            }
            break;
        }

        // update show
        new AsyncTask<Void, Void, Void>() {
            private PieChartData mCVData;

            @Override
            protected void onPreExecute() {
                showProgress(true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                mCVData = new PieChartData();
                generateData(mCVData);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                showProgress(false);

                mCVchart.setCircleFillRatio(0.6f);
                mCVchart.setPieChartData(mCVData);
            }
        }.execute();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mPBLoadData.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoadData.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoadData.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 重新加载数据
     */
    private void loadData() {
        mLLOrgData = new LinkedList<>();
        new AsyncTask<Void, Void, Void>() {
            private PieChartData mCVData;

            @Override
            protected void onPreExecute() {
                showProgress(true);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (!UtilFun.ListIsNullOrEmpty(mASParaLoad)) {
                    if (2 != mASParaLoad.size())
                        return null;

                    String d_s = mASParaLoad.get(0);
                    String d_e = mASParaLoad.get(1);
                    HashMap<String, ArrayList<INote>> hm_note = NoteDataHelper.getInstance()
                            .getNotesBetweenDays(d_s, d_e);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        hm_note.values().forEach(mLLOrgData::addAll);
                    } else {
                        for (ArrayList<INote> ls_n : hm_note.values()) {
                            mLLOrgData.addAll(ls_n);
                        }
                    }

                    mCVData = new PieChartData();
                    generateData(mCVData);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                showProgress(false);

                mCVchart.setCircleFillRatio(0.6f);
                mCVchart.setPieChartData(mCVData);
            }
        }.execute();
    }


    /**
     * 生成数据
     */
    private void generateData(PieChartData pd) {
        class chartItem {
            private final static int PAY_ITEM = 1;
            private final static int INCOME_ITEM = 2;

            private int mType;
            private String mSZName;
            private BigDecimal mBDVal;


            /**
             * 更新节点链表
             * @param ls_datas  节点链表
             */
            private void updateList(List<chartItem> ls_datas) {
                boolean bf = false;
                for (chartItem ci : ls_datas) {
                    if (ci.mSZName.equals(mSZName) && ci.mType == mType) {
                        bf = true;
                        ci.mBDVal = ci.mBDVal.add(mBDVal);
                        break;
                    }
                }

                if (!bf) {
                    ls_datas.add(this);
                }
            }
        }

        // create chart item list
        boolean b_p = mTBPay.isChecked();
        boolean b_i = mTBIncome.isChecked();
        LinkedList<chartItem> ls_ci = new LinkedList<>();
        for (INote data : mLLOrgData) {
            if (data.isPayNote() && !b_p)
                continue;

            if (data.isIncomeNote() && !b_i)
                continue;

            chartItem ci = new chartItem();
            ci.mBDVal = data.getVal();
            ci.mType = data.isPayNote() ? chartItem.PAY_ITEM : chartItem.INCOME_ITEM;
            ci.mSZName = data.getInfo();

            ci.updateList(ls_ci);
        }

        // create values
        List<SliceValue> values = new ArrayList<>();
        for (chartItem ci : ls_ci) {

            SliceValue sliceValue = new SliceValue(ci.mBDVal.floatValue(), ChartUtils.pickColor());
            //sliceValue.setLabel((ci.mType == chartItem.PAY_ITEM ? "p " : "i ")
            //                        + ci.mSZName);
            sliceValue.setLabel(ci.mSZName);
            values.add(sliceValue);
        }

        pd.setValues(values);
        pd.setHasLabels(true);
        pd.setHasLabelsOutside(true);
        pd.setHasCenterCircle(true);
        pd.setSlicesSpacing(12);

        // hasCenterText1
        pd.setCenterText1(b_p && b_i ? "收支" : (b_p ? "支出" : "收入"));

        // Get roboto-italic font.
        //Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
        //mCVData.setCenterText1Typeface(tf);

        // Get font size from dimens.xml and convert it to sp(library uses sp values).
        pd.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
    }
}
