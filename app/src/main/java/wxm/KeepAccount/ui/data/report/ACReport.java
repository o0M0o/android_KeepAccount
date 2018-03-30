package wxm.KeepAccount.ui.data.report;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import wxm.KeepAccount.ui.base.ExtendActivity.ACSwitcherActivity;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;

/**
 * UI for report
 * Created by ookoo on 2017/2/15.
 */
public class ACReport extends ACSwitcherActivity<android.support.v4.app.Fragment> {
    public final static String PARA_TYPE = "para_type";
    public final static String PT_DAY = "pt_day";
    public final static String PT_MONTH = "pt_month";
    public final static String PT_YEAR = "pt_year";

    public final static String PARA_LOAD = "para_load";

    private android.support.v4.app.Fragment    mSelfFrg;

    @Override
    protected void initUi(Bundle savedInstanceState)    {
        super.initUi(savedInstanceState);

        LOG_TAG = "ACNoteAdd";

        // check invoke intent
        Intent it = getIntent();
        String sz_type = it.getStringExtra(PARA_TYPE);
        if (UtilFun.StringIsNullOrEmpty(sz_type)) {
            Log.e(LOG_TAG, "调用intent缺少'PARA_TYPE'参数");
            return;
        }

        ArrayList<String> al_load = it.getStringArrayListExtra(PARA_LOAD);
        if (UtilFun.ListIsNullOrEmpty(al_load)) {
            Log.e(LOG_TAG, "调用intent缺少'PARA_LOAD'参数");
            return;
        }

        // for holder
        switch (sz_type) {
            case PT_DAY: {
                mSelfFrg = new FrgReportDay();
            }
            break;

            case PT_MONTH: {
                mSelfFrg = new FrgReportMonth();
            }
            break;

            case PT_YEAR: {
                mSelfFrg = new FrgReportYear();
            }
            break;
        }

        if (null != mSelfFrg) {
            Bundle bd = new Bundle();
            bd.putStringArrayList(PARA_LOAD, al_load);
            mSelfFrg.setArguments(bd);
        }

        addChildFrg(mSelfFrg);
        loadHotFrg();
    }

    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;
        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }
}
