package wxm.KeepAccount.ui.data.report;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import wxm.androidutil.Dialog.DlgOKOrNOBase;
import wxm.androidutil.FrgUtility.FrgUtilityBase;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.data.report.page.DayReportChart;
import wxm.KeepAccount.ui.data.report.page.DayReportWebView;
import wxm.KeepAccount.ui.dialog.DlgSelectReportDays;
import wxm.KeepAccount.ui.utility.NoteDataHelper;

/**
 * day data report
 * Created by ookoo on 2017/2/15.
 */
public class FrgReportDay extends FrgUtilityBase {
    @BindView(R.id.tv_day)
    TextView mTVDay;
    @BindView(R.id.tv_pay)
    TextView mTVPay;
    @BindView(R.id.tv_income)
    TextView mTVIncome;
    private ArrayList<String> mASParaLoad;
    private FrgUtilityBase mPGHot = null;
    private DayReportWebView mPGWebView = new DayReportWebView();
    private DayReportChart mPGChart = new DayReportChart();

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
     * update date range
     * @param event     event with start & end day
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectDaysEvent(EventSelectDays event) {
        mASParaLoad.set(0, event.mSZStartDay);
        mASParaLoad.set(1, event.mSZEndDay);

        loadUI();
    }

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
        Activity h = this.getActivity();
        FrgReportDay frg = this;
        ExecutorService tp = Executors.newCachedThreadPool();
        tp.submit(() -> {
            if (!UtilFun.ListIsNullOrEmpty(frg.mASParaLoad)) {
                if (2 != frg.mASParaLoad.size())
                    return;

                String d_s = frg.mASParaLoad.get(0);
                String d_e = frg.mASParaLoad.get(1);
                final String mSZCaption = String.format(Locale.CHINA,
                        "%s - %s", d_s, d_e);
                HashMap<String, ArrayList<INote>> ls_note = NoteDataHelper.getInstance()
                        .getNotesBetweenDays(d_s, d_e);

                BigDecimal mBDTotalPay = BigDecimal.ZERO;
                BigDecimal mBDTotalIncome = BigDecimal.ZERO;
                for (ArrayList<INote> ls_n : ls_note.values()) {
                    for (INote id : ls_n) {
                        if (id.isPayNote())
                            mBDTotalPay = mBDTotalPay.add(id.getVal());
                        else
                            mBDTotalIncome = mBDTotalIncome.add(id.getVal());
                    }
                }

                final BigDecimal t_p = mBDTotalPay;
                final BigDecimal t_i = mBDTotalIncome;
                if(!(h.isFinishing() || h.isDestroyed())) {
                    h.runOnUiThread(() -> {
                        frg.mTVDay.setText(mSZCaption);
                        frg.mTVPay.setText(String.format(Locale.CHINA,
                                "%.02f", t_p.floatValue()));
                        frg.mTVIncome.setText(String.format(Locale.CHINA,
                                "%.02f", t_i.floatValue()));
                    });
                }
            }
        });
    }

    /**
     * 切换显示类型
     *
     * @param v 动作view
     */
    @OnClick({R.id.iv_switch})
    public void onSwitchShow(View v) {
        switchPage();
    }

    /**
     * 重新选择起止时间
     *
     * @param v 动作view
     */
    @OnClick({R.id.tv_select_days})
    public void onSelectDays(View v) {
        DlgSelectReportDays dlg_days = new DlgSelectReportDays();
        dlg_days.addDialogListener(new DlgOKOrNOBase.DialogResultListener() {
            @Override
            public void onDialogPositiveResult(DialogFragment dialogFragment) {
                EventBus.getDefault().post(new EventSelectDays(dlg_days.getStartDay(),
                        dlg_days.getEndDay()));
            }

            @Override
            public void onDialogNegativeResult(DialogFragment dialogFragment) {
            }
        });

        dlg_days.show(((AppCompatActivity) getActivity()).getSupportFragmentManager()
                , "select days");
    }


    /**
     * 切换展示类型
     */
    private void switchPage() {
        mPGHot = mPGHot instanceof DayReportWebView ? mPGChart : mPGWebView;

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.fl_page_holder, mPGHot);
        t.commit();
    }
}
