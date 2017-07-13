package wxm.KeepAccount.ui.help;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import wxm.androidutil.FrgWebView.FrgWebView;

/**
 * for help
 * Created by ookoo on 2016/11/29.
 */
public class FrgHelp extends FrgWebView {
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = super.inflaterView(inflater, container, bundle);
        LOG_TAG = "FrgHelp";
        return rootView;
    }

    @Override
    protected void onWVPageFinished(WebView wvPage, Object pagePara) {
    }


    @Override
    protected void loadUI() {
        loadPage("file:///android_asset/help/help_main.html", null);
    }
}
