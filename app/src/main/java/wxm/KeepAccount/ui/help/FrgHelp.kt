package wxm.KeepAccount.ui.help


import android.os.Bundle
import wxm.androidutil.frgUtil.FrgSupportWebView


/**
 * for help
 * Created by WangXM on 2016/11/29.
 */
class FrgHelp : FrgSupportWebView() {
    override fun loadUI(bundle: Bundle?) {
        loadPage("file:///android_asset/help/help_main.html", null)
    }
}
