package wxm.KeepAccount.ui.data.edit.Note.webview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cn.wxm.andriodutillib.ExActivity.BaseAppCompatActivity;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.ui.data.edit.Note.ACNoteEdit;
import wxm.KeepAccount.ui.data.edit.Note.FrgNoteEdit;

import static wxm.KeepAccount.ui.data.edit.Note.ACNoteEdit.PARA_ACTION;

/**
 * use webview to do note edit
 * Created by ookoo on 2017/6/23.
 */
public class ACWVNoteEdit extends BaseAppCompatActivity {
    private String mAction;

    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initFrgHolder() {
        LOG_TAG = "ACWVNoteEdit";

        Intent it = getIntent();
        assert null != it;
        mAction = it.getStringExtra(PARA_ACTION);
        if (UtilFun.StringIsNullOrEmpty(mAction)) {
            Log.e(LOG_TAG, "调用intent缺少'PARA_ACTION'参数");
            return;
        }

        // for holder
        mFGHolder = new FrgWVNoteEdit();
        Bundle bd = new Bundle();
        String date = it.getStringExtra(GlobalDef.STR_RECORD_DATE);
        if (!UtilFun.StringIsNullOrEmpty(date)) {
            bd.putString(GlobalDef.STR_RECORD_DATE, date);
        }

        bd.putString(PARA_ACTION, mAction);
        if (mAction.equals(GlobalDef.STR_MODIFY)) {
            int pid = it.getIntExtra(ACNoteEdit.PARA_NOTE_PAY, GlobalDef.INVALID_ID);
            int iid = it.getIntExtra(ACNoteEdit.PARA_NOTE_INCOME, GlobalDef.INVALID_ID);
            if (GlobalDef.INVALID_ID != pid) {
                bd.putInt(ACNoteEdit.PARA_NOTE_PAY, pid);
            } else if (GlobalDef.INVALID_ID != iid) {
                bd.putInt(ACNoteEdit.PARA_NOTE_INCOME, iid);
            } else {
                Log.e(LOG_TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return;
            }
        }
        mFGHolder.setArguments(bd);
    }
}
