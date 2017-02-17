package wxm.KeepAccount.ui.fragment.utility;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.INote;
import wxm.KeepAccount.Base.utility.NotesToHtmlUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.DataBase.NoteShowDataHelper;
import wxm.KeepAccount.ui.acutility.ACReport;

/**
 * 日数据汇报
 * Created by ookoo on 2017/2/15.
 */
public class FrgReportDay extends FrgUtilityBase {
    private ArrayList<String>   mASParaLoad;

    //private SimpleDateFormat mDFFormat = new SimpleDateFormat( "yyyy-MM-dd", Locale.CHINA);

    @BindView(R.id.wv_report)
    WebView     mWVReport;

    @BindView(R.id.pb_load_data)
    ProgressBar mPBLoadData;

    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgReportDay";
        View rootView = layoutInflater.inflate(R.layout.vw_report, viewGroup, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
        Bundle bd = getArguments();
        mASParaLoad = bd.getStringArrayList(ACReport.PARA_LOAD);
    }

    @Override
    protected void initUiInfo() {
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
                    String sz_caption = String.format(Locale.CHINA,
                                            "%s - %s", d_s, d_e);
                    List<INote> ls_note = NoteShowDataHelper.getInstance()
                                                .getNotesBetweenDays(d_s, d_e);
                    mSZHtml = NotesToHtmlUtil.NotesToHtmlStr(sz_caption, ls_note);
                    Log.d(LOG_TAG, "initUiInfo html : " +
                                (UtilFun.StringIsNullOrEmpty(mSZHtml) ? "null" : mSZHtml));
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
