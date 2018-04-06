package wxm.KeepAccount.ui.help;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wxm.androidutil.FrgWebView.FrgWebView;

/**
 * for help
 * Created by WangXM on 2016/11/29.
 */
public class FrgHelp extends FrgWebView {
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return super.inflaterView(inflater, container, bundle);
    }

    @Override
    protected void loadUI(Bundle bundle) {
        loadPage("file:///android_asset/help/help_main.html", null);
    }
}
