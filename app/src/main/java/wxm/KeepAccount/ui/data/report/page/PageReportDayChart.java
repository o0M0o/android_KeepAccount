package wxm.KeepAccount.ui.data.report.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
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

    private PieChartData mCVData;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "PageReportDayWebView";
        View rootView = layoutInflater.inflate(R.layout.page_report_chart, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);
    }

    @Override
    protected void loadUI() {
        //Toast.makeText(getActivity(), "In PageReportDayChart", Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Void>() {
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

                    LinkedList<INote> ls_notes = new LinkedList<>();
                    hm_note.values().forEach(ls_notes::addAll);
                    generateData(ls_notes);
                }

                return null;
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // After completing execution of given task, control will return here.
                // Hence if you want to populate UI elements with fetched data, do it here.
                showProgress(false);

                mCVchart.setCircleFillRatio(0.7f);
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
     * @param ls_data  原始数据
     */
    private void generateData(List<INote> ls_data) {
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
        LinkedList<chartItem>  ls_ci      = new LinkedList<>();
        for(INote data : ls_data)   {
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
            sliceValue.setLabel((ci.mType == chartItem.PAY_ITEM ? "pay " : "income ")
                                    + ci.mSZName);
            values.add(sliceValue);
        }

        mCVData = new PieChartData(values);
        mCVData.setHasLabels(true);
        mCVData.setHasLabelsOutside(true);
        mCVData.setHasCenterCircle(true);
        mCVData.setSlicesSpacing(24);

        // hasCenterText1
        mCVData.setCenterText1("Hello!");

        // Get roboto-italic font.
        //Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
        //mCVData.setCenterText1Typeface(tf);

        // Get font size from dimens.xml and convert it to sp(library uses sp values).
        mCVData.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
    }
}
