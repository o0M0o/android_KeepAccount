package wxm.KeepAccount.ui.data.edit.Note;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.ExActivity.BaseAppCompatActivity;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.edit.base.TFEditBase;

/**
 * 支出/收入数据编辑UI
 */
public class ACNoteEdit extends BaseAppCompatActivity {
    public static final String  PARA_ACTION          = "para_action";
    public static final String  PARA_NOTE_PAY        = "note_pay";
    public static final String  PARA_NOTE_INCOME     = "note_income";

    public static final int DEF_NOTE_MAXLEN = 200;
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
        LOG_TAG = "ACNoteEdit";

        Intent it = getIntent();
        assert null != it;
        mAction = it.getStringExtra(PARA_ACTION);
        if(UtilFun.StringIsNullOrEmpty(mAction)) {
            Log.e(LOG_TAG, "调用intent缺少'PARA_ACTION'参数");
            return ;
        }

        // for holder
        mFGHolder = new FrgNoteEdit();
        Bundle bd = new Bundle();
        String date = it.getStringExtra(GlobalDef.STR_RECORD_DATE);
        if(!UtilFun.StringIsNullOrEmpty(date))  {
            bd.putString(GlobalDef.STR_RECORD_DATE, date);
        }

        bd.putString(PARA_ACTION, mAction);
        if(mAction.equals(GlobalDef.STR_MODIFY)) {
            int pid = it.getIntExtra(ACNoteEdit.PARA_NOTE_PAY, GlobalDef.INVALID_ID);
            int iid = it.getIntExtra(ACNoteEdit.PARA_NOTE_INCOME, GlobalDef.INVALID_ID);
            if(GlobalDef.INVALID_ID != pid)   {
                bd.putInt(ACNoteEdit.PARA_NOTE_PAY, pid);
            } else if(GlobalDef.INVALID_ID != iid)    {
                bd.putInt(ACNoteEdit.PARA_NOTE_INCOME, iid);
            } else  {
                Log.e(LOG_TAG, "调用intent缺少'PARA_NOTE_PAY'和'PARA_NOTE_INCOME'参数");
                return;
            }
        }
        mFGHolder.setArguments(bd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_record_edit, menu);
        return true;
    }


    /**
     * 得到当前选中的tab item
     * @return  当前选中的tab item
     */
    private TFEditBase getHotTabItem() {
        return ((FrgNoteEdit)mFGHolder).getHotTabItem();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                TFEditBase tb = getHotTabItem();
                if(tb.onAccept()) {
                    finish();
                }
            }
            break;

            case R.id.mi_giveup: {
                int ret_data = GlobalDef.INTRET_GIVEUP;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
