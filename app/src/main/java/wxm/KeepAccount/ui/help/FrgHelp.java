package wxm.KeepAccount.ui.help;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import wxm.KeepAccount.R;

/**
 * for help
 * Created by ookoo on 2016/11/29.
 */
public class FrgHelp extends FrgUtilityBase {

    //private static String LOG_TAG = "ACHelp";
    private static final String ENCODING = "utf-8";
    //private static final String MIMETYPE = "text/html; charset=UTF-8";

    // for ui
    @BindView(R.id.wv_help)
    WebView     mWVHelp;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        LOG_TAG = "FrgHelp";
        View rootView = inflater.inflate(R.layout.vw_help, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initUiComponent(View view) {
    }

    @Override
    protected void loadUI() {
        load_help("file:///android_asset/help_main.html");
    }

    /// PRIVATE BEGIN
    /**
     * 加载帮助html
     * @param url  帮助html路径
     */
    private void load_help(String url) {
        WebSettings wSet = mWVHelp.getSettings();
        wSet.setDefaultTextEncodingName(ENCODING);

        mWVHelp.loadUrl(url);
    }
    /// PRIVATE END
}
