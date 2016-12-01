package wxm.KeepAccount.ui.acutility;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.HashMap;

import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.AppGobalDef;
import wxm.KeepAccount.Base.data.AppModel;
import wxm.KeepAccount.Base.db.IncomeNoteItem;
import wxm.KeepAccount.Base.db.PayNoteItem;
import wxm.KeepAccount.Base.define.BaseAppCompatActivity;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.acinterface.ACHelp;
import wxm.KeepAccount.ui.fragment.base.TFEditBase;
import wxm.KeepAccount.ui.fragment.EditData.TFEditIncome;
import wxm.KeepAccount.ui.fragment.EditData.TFEditPay;
import wxm.KeepAccount.ui.fragment.utility.FrgNoteEdit;

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
        int ret_data = AppGobalDef.INTRET_GIVEUP;

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
        bd.putString(PARA_ACTION, mAction);
        if(mAction.equals(AppGobalDef.STR_MODIFY)) {
            int pid = it.getIntExtra(ACNoteEdit.PARA_NOTE_PAY, AppGobalDef.INVALID_ID);
            int iid = it.getIntExtra(ACNoteEdit.PARA_NOTE_INCOME, AppGobalDef.INVALID_ID);
            if(AppGobalDef.INVALID_ID != pid)   {
                bd.putInt(ACNoteEdit.PARA_NOTE_PAY, pid);
            } else if(AppGobalDef.INVALID_ID != iid)    {
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
        inflater.inflate(R.menu.acm_record_actbar, menu);
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
            case R.id.record_memenu_save: {
                TFEditBase tb = getHotTabItem();
                if(tb.onAccept()) {
                    Intent data = new Intent();
                    setResult(mAction.equals(AppGobalDef.STR_CREATE) ?  AppGobalDef.INTRET_RECORD_ADD
                                : AppGobalDef.INTRET_RECORD_MODIFY,  data);
                    finish();
                }
            }
            break;

            case R.id.record_menu_giveup: {
                int ret_data = AppGobalDef.INTRET_GIVEUP;

                Intent data = new Intent();
                setResult(ret_data, data);
                finish();
            }
            break;

            case R.id.recordmenu_help : {
                Intent intent = new Intent(this, ACHelp.class);
                intent.putExtra(ACHelp.STR_HELP_TYPE, ACHelp.STR_HELP_RECORD);

                startActivityForResult(intent, 1);
            }
            break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /*
    @Override
    protected void onDataChange(int requestCode, int resultCode, Intent data) {
        super.onDataChange(requestCode, resultCode, data);
        if(mTabFragment instanceof STEditNoteFragment)  {
            STEditNoteFragment sf = UtilFun.cast(mTabFragment);
            sf.onDataChange(requestCode, resultCode, data);
        }
    }
    */
}
