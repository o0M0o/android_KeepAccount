package wxm.KeepAccount.ui.data.edit.Note;

import android.content.Intent;
import android.os.Bundle;

import cn.wxm.andriodutillib.ExActivity.BaseAppCompatActivity;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;

/**
 * 支出/收入数据编辑UI
 */
public class ACNoteAdd extends BaseAppCompatActivity {
    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACNoteAdd";

        Intent it = getIntent();
        assert null != it;

        // for holder
        mFGHolder = new FrgNoteAdd();
        Bundle bd = new Bundle();
        String date = it.getStringExtra(GlobalDef.STR_RECORD_DATE);
        if (!UtilFun.StringIsNullOrEmpty(date)) {
            bd.putString(GlobalDef.STR_RECORD_DATE, date);
        }
        mFGHolder.setArguments(bd);
    }
}
