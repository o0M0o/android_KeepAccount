package wxm.KeepAccount.ui.data.report;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import cn.wxm.andriodutillib.ExActivity.BaseAppCompatActivity;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;

/**
 * 显示汇报信息
 * Created by ookoo on 2017/2/15.
 */
public class ACReport extends BaseAppCompatActivity {
    public final static String PARA_TYPE = "para_type";
    public final static String PT_DAY = "pt_day";
    public final static String PT_MONTH = "pt_month";
    public final static String PT_YEAR = "pt_year";


    public final static String PARA_LOAD = "para_load";


    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;
        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACNoteEdit";

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
                mFGHolder = new FrgReportDay();
            }
            break;

            case PT_MONTH: {
                mFGHolder = new FrgReportMonth();
            }
            break;

            case PT_YEAR: {
                mFGHolder = new FrgReportYear();
            }
            break;
        }

        if (null != mFGHolder) {
            Bundle bd = new Bundle();
            bd.putStringArrayList(PARA_LOAD, al_load);
            mFGHolder.setArguments(bd);
        }
    }
}
