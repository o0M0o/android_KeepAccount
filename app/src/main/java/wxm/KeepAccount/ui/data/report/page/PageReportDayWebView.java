package wxm.KeepAccount.ui.data.report.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.utility.NoteShowDataHelper;

/**
 * 日数据汇报 - webview展示页
 * Created by ookoo on 2017/3/4.
 */
public class PageReportDayWebView extends FrgUtilityBase {
    private ArrayList<String>   mASParaLoad;

    @BindView(R.id.wv_report)
    WebView         mWVReport;

    @BindView(R.id.pb_load_data)
    ProgressBar     mPBLoadData;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "PageReportDayWebView";
        View rootView = layoutInflater.inflate(R.layout.page_report_webview, viewGroup, false);
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
        //Toast.makeText(getActivity(), "In PageReportDayWebView", Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Void>() {
            private String  mSZHtml;

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
                    HashMap<String, ArrayList<INote>> ls_note = NoteShowDataHelper.getInstance()
                                                .getNotesBetweenDays(d_s, d_e);


                    mSZHtml = NotesToHtmlUtil.NotesToHtmlStr(ls_note);
                    //Log.d(LOG_TAG, "initUiInfo html : " +
                    //            (UtilFun.StringIsNullOrEmpty(mSZHtml) ? "null" : mSZHtml));
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

                // for web show
                if(!UtilFun.StringIsNullOrEmpty(mSZHtml)) {
                    mWVReport.getSettings().setDefaultTextEncodingName("utf-8");
                    mWVReport.getSettings().setJavaScriptEnabled(true);
                    mWVReport.loadDataWithBaseURL(null, mSZHtml, "text/html; utf-8", null, null);
                }
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
}
