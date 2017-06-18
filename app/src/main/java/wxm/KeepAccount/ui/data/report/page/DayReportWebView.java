package wxm.KeepAccount.ui.data.report.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.BuildConfig;
import wxm.KeepAccount.R;
import wxm.KeepAccount.define.INote;
import wxm.KeepAccount.ui.data.report.ACReport;
import wxm.KeepAccount.ui.data.report.EventSelectDays;
import wxm.KeepAccount.ui.utility.NoteDataHelper;

/**
 * 日数据汇报 - webview展示页
 * Created by ookoo on 2017/3/4.
 */
public class DayReportWebView extends FrgUtilityBase {
    @BindView(R.id.wv_report)
    WebView mWVReport;
    @BindView(R.id.pb_load_data)
    ProgressBar mPBLoadData;
    private ArrayList<String> mASParaLoad;

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
        loadUI();
    }

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "DayReportWebView";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

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
        new AsyncTask<Void, Void, Void>() {
            private String mSzPara;

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
                    HashMap<String, ArrayList<INote>> hmData = NoteDataHelper
                                            .getInstance().getNotesBetweenDays(d_s, d_e);

                    SimplePropertyPreFilter filter = new SimplePropertyPreFilter(INote.class,
                                    "info", "ts", "val", "payNote");
                    mSzPara = JSON.toJSONString(hmData, filter);
                }

                return null;
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                showProgress(false);

                if(!UtilFun.StringIsNullOrEmpty(mSzPara)) {
                    mWVReport.getSettings().setDefaultTextEncodingName("utf-8");
                    mWVReport.getSettings().setJavaScriptEnabled(true);
                    //mWVReport.loadDataWithBaseURL(null, mSZHtml, "text/html; utf-8", null, null);
                    mWVReport.loadUrl("file:///android_asset/report/report_day.html");

                    mWVReport.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                            super.onPageFinished(view, url);
                            mWVReport.evaluateJavascript(
                                    "onLoadData(" + mSzPara + ")",
                                    value -> {
                            });
                        }
                    });
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
