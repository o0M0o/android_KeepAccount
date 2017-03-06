package wxm.KeepAccount.ui.data.report.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import wxm.KeepAccount.define.IncomeNoteItem;
import wxm.KeepAccount.define.PayNoteItem;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;

/**
 * 日数据汇报 - webview展示页
 * Created by ookoo on 2017/3/4.
 */
public class PageReportDayChart extends FrgUtilityBase {
    private ArrayList<String>   mASParaLoad;

    @BindView(R.id.chart)
    PieChartView    mCVchart;

    @BindView(R.id.pb_load_data)
    ProgressBar     mPBLoadData;

    @BindView(R.id.sw_income)
    Switch          mSWIncome;

    @BindView(R.id.sw_pay)
    Switch          mSWPay;

    private LinkedList<INote>   mLLOrgData;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "PageReportDayChart";
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
        mLLOrgData = new LinkedList<>();

        new AsyncTask<Void, Void, Void>() {
            private PieChartData        mCVData;

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
                    HashMap<String, ArrayList<INote>> hm_note = NoteShowDataHelper.getInstance()
                            .getNotesBetweenDays(d_s, d_e);

                    hm_note.values().forEach(mLLOrgData::addAll);

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

    @Override
    protected void loadUI() {
    }

    /**
     * 切换显示内容
     * @param v     激活的switch
     */
    @OnClick({R.id.sw_income, R.id.sw_pay})
    public void onSWClick(View v)   {
        Log.d(LOG_TAG, "in onSWClick");

        int vid = v.getId();
        switch (vid)    {
            case R.id.sw_income :   {
                if(mSWIncome.isChecked())   {
                    mSWPay.setClickable(true);
                } else  {
                    mSWPay.setClickable(false);
                }
            }
            break;

            case R.id.sw_pay :   {
                if(mSWPay.isChecked())   {
                    mSWIncome.setClickable(true);
                } else  {
                    mSWIncome.setClickable(false);
                }
            }
            break;
        }

        // update show
        new AsyncTask<Void, Void, Void>() {
            private PieChartData        mCVData;

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
     * 生成数据
     */
    private void generateData(PieChartData pd) {
        class chartItem {
            private final static int PAY_ITEM        = 1;
            private final static int INCOME_ITEM     = 2;

            private int          mType;
            private String       mSZName;
            private BigDecimal   mBDVal;


            /**
             * 更新节点链表
             * @param ls_datas  节点链表
             */
            private void updateList(List<chartItem> ls_datas)    {
                boolean bf = false;
                for(chartItem ci : ls_datas)    {
                    if(ci.mSZName.equals(mSZName) && ci.mType == mType)  {
                        bf = true;
                        ci.mBDVal = ci.mBDVal.add(mBDVal);
                        break;
                    }
                }

                if(!bf) {
                    ls_datas.add(this);
                }
            }
        }

        // create chart item list
        boolean b_p = mSWPay.isChecked();
        boolean b_i = mSWIncome.isChecked();
        LinkedList<chartItem>  ls_ci      = new LinkedList<>();
        for(INote data : mLLOrgData)   {
            if(data.isPayNote() && !b_p)
                continue;

            if(data.isIncomeNote() && !b_i)
                continue;

            chartItem ci = new chartItem();
            ci.mBDVal = data.getVal();
            ci.mType = data.isPayNote() ? chartItem.PAY_ITEM : chartItem.INCOME_ITEM;
            ci.mSZName = data.getInfo();

            ci.updateList(ls_ci);
        }

        // create values
        List<SliceValue> values = new ArrayList<>();
        for(chartItem ci : ls_ci)   {

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
