package wxm.KeepAccount.ui.help;


import android.os.Bundle;
import wxm.androidutil.FrgWebView.FrgSupportWebView;


/**
 * for help
 * Created by WangXM on 2016/11/29.
 */
public class FrgHelp extends FrgSupportWebView {
    @Override
    protected void loadUI(Bundle bundle) {
        loadPage("file:///android_asset/help/help_main.html", null);
    }
}
