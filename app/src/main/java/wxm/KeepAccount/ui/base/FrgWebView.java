package wxm.KeepAccount.ui.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.KeepAccount.BuildConfig;
import wxm.KeepAccount.R;

/**
 * fragment for webView
 * Created by ookoo on 2017/2/15.
 */
public abstract class FrgWebView extends FrgUtilityBase {
    @BindView(R.id.wv_page)
    WebView mWVPage;

    @BindView(R.id.pb_load)
    ProgressBar mPBLoad;

    private Object pagePara;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected View inflaterView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LOG_TAG = "FrgWebView";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        View rootView = layoutInflater.inflate(R.layout.frg_webview, viewGroup, false);
        ButterKnife.bind(this, rootView);

        mWVPage.getSettings().setDefaultTextEncodingName("utf-8");
        mWVPage.getSettings().setJavaScriptEnabled(true);
        mWVPage.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                onWVPageFinished(mWVPage, pagePara);
            }
        });
        return rootView;
    }

    /**
     * webview加载完后调用
     *
     * @param wvPage   object for page
     * @param pagePara para for page
     */
    protected abstract void onWVPageFinished(WebView wvPage, Object pagePara);

    /**
     * 加载页
     *
     * @param pageUrl  页url
     * @param pagePara para
     */
    protected void loadPage(String pageUrl, Object pagePara) {
        this.pagePara = pagePara;
        mWVPage.loadUrl(pageUrl);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    protected void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources()
                .getInteger(android.R.integer.config_shortAnimTime);

        mPBLoad.setVisibility(show ? View.VISIBLE : View.GONE);
        mPBLoad.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPBLoad.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
