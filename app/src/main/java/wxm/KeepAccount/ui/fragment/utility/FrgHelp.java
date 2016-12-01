package wxm.KeepAccount.ui.fragment.utility;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wxm.andriodutillib.FrgUtility.FrgUtilityBase;
import cn.wxm.andriodutillib.util.UtilFun;
import cn.wxm.andriodutillib.util.WRMsgHandler;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppMsgDef;
import wxm.KeepAccount.Base.db.UsrItem;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACWelcome;
import wxm.KeepAccount.ui.acutility.ACAddUsr;

/**
 * for help
 * Created by ookoo on 2016/11/29.
 */
public class FrgHelp extends FrgUtilityBase {


    //private static String TAG = "ACHelp";
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
    protected void initUiInfo() {
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
