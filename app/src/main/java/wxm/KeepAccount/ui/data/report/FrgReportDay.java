package wxm.KeepAccount.ui.data.report;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.report.page.NotesToHtmlUtil;
import wxm.KeepAccount.ui.data.report.page.PageReportDayChart;
import wxm.KeepAccount.ui.data.report.page.PageReportDayWebView;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;

/**
 * 日数据汇报
 * Created by ookoo on 2017/2/15.
 */
public class FrgReportDay extends FrgUtilityBase {
    private ArrayList<String>   mASParaLoad;

    @BindView(R.id.tv_day)
    TextView    mTVDay;

    @BindView(R.id.tv_pay)
    TextView    mTVPay;

    @BindView(R.id.tv_income)
    TextView    mTVIncome;

    private FrgUtilityBase          mPGHot = null;
    private PageReportDayWebView    mPGWebView = new PageReportDayWebView();
    private PageReportDayChart      mPGChart = new PageReportDayChart();

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgReportDay";
        View rootView = layoutInflater.inflate(R.layout.vw_report, viewGroup, false);
        ButterKnife.bind(this, rootView);

        mPGHot = mPGWebView;
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);

        mPGWebView.setArguments(bd);
        mPGChart.setArguments(bd);

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.fl_page_holder, mPGHot);
        t.commit();
    }

    @Override
    protected void loadUI() {
        new AsyncTask<Void, Void, Void>() {
            private String  mSZCaption;

            private BigDecimal  mBDTotalPay     = BigDecimal.ZERO;
            private BigDecimal  mBDTotalIncome  = BigDecimal.ZERO;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (!UtilFun.ListIsNullOrEmpty(mASParaLoad)) {
                    if (2 != mASParaLoad.size())
                        return null;

                    String d_s = mASParaLoad.get(0);
                    String d_e = mASParaLoad.get(1);
                    mSZCaption = String.format(Locale.CHINA,
                                            "%s - %s", d_s, d_e);
                    HashMap<String, ArrayList<INote>> ls_note = NoteShowDataHelper.getInstance()
                                                .getNotesBetweenDays(d_s, d_e);

                    for(ArrayList<INote> ls_n : ls_note.values()) {
                        for(INote id : ls_n)    {
                            if(id.isPayNote())
                                mBDTotalPay = mBDTotalPay.add(id.getVal());
                            else
                                mBDTotalIncome = mBDTotalIncome.add(id.getVal());
                        }
                    }
                }

                return null;
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // After completing execution of given task, control will return here.
                // Hence if you want to populate UI elements with fetched data, do it here.

                // for header show
                mTVDay.setText(mSZCaption);
                mTVPay.setText(String.format(Locale.CHINA,
                                    "%.02f", mBDTotalPay.floatValue()));
                mTVIncome.setText(String.format(Locale.CHINA,
                                    "%.02f", mBDTotalIncome.floatValue()));
            }
        }.execute();
    }

    /**
     * 切换显示类型
     * @param v   动作view
     */
    @OnClick({R.id.iv_switch})
    public void onSwitchShow(View v) {
        switchPage();
    }


    /**
     * 切换展示类型
     */
    private void switchPage()   {
        mPGHot = mPGHot instanceof PageReportDayWebView ? mPGChart : mPGWebView;

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.fl_page_holder, mPGHot);
        t.commit();
    }
}
