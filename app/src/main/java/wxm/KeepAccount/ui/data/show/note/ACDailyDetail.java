package wxm.KeepAccount.ui.data.show.note;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import wxm.androidutil.Switcher.ACSwitcherActivity;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;

/**
 * day detail UI
 */
public class ACDailyDetail extends ACSwitcherActivity<FrgDailyDetail> {
    public final static String K_HOTDAY = "hotday";

    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void setupFragment(Bundle bundle) {
        Intent it = getIntent();
        assert null != it;
        String hot_day = it.getStringExtra(K_HOTDAY);
        if (UtilFun.StringIsNullOrEmpty(hot_day)) {
            Log.e(LOG_TAG, "调用intent缺少'K_HOTDAY'参数");
            return;
        }

        // for holder
        FrgDailyDetail fg = new FrgDailyDetail();
        Bundle bd = new Bundle();
        bd.putString(K_HOTDAY, hot_day);
        fg.setArguments(bd);

        addFragment(fg);
    }
}
