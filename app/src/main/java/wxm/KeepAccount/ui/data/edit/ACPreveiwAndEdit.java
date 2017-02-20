package wxm.KeepAccount.ui.data.edit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.wxm.andriodutillib.ExActivity.BaseAppCompatActivity;
import cn.wxm.andriodutillib.util.UtilFun;
import wxm.KeepAccount.Base.data.BudgetItem;
import wxm.KeepAccount.Base.data.IncomeNoteItem;
import wxm.KeepAccount.Base.data.PayNoteItem;
import wxm.KeepAccount.Base.db.BudgetDBUtility;
import wxm.KeepAccount.Base.db.PayIncomeDBUtility;
import wxm.KeepAccount.Base.define.GlobalDef;
import wxm.KeepAccount.Base.utility.ContextUtil;
import wxm.KeepAccount.R;
import wxm.KeepAccount.ui.data.edit.base.ITFBase;

public class ACPreveiwAndEdit extends BaseAppCompatActivity {
    private ITFBase     mTFBase;

    private final static String     CHANGETO_PREVIEW = "预览";
    private final static String     CHANGETO_EDIT    = "编辑";

    private MenuItem    mMISwitch;
    private MenuItem    mMISave;

    @Override
    protected void initUi(Bundle savedInstanceState) {
        Intent it = getIntent();
        String type = it.getStringExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE);
        if(UtilFun.StringIsNullOrEmpty(type)
                || (!GlobalDef.STR_RECORD_PAY.equals(type) && !GlobalDef.STR_RECORD_INCOME.equals(type)
                && !GlobalDef.STR_RECORD_BUDGET.equals(type)))
            return;

        super.initUi(savedInstanceState);
    }

    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    protected void initFrgHolder() {
        Intent it = getIntent();
        String type = it.getStringExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE);
        if(UtilFun.StringIsNullOrEmpty(type)
                || (!GlobalDef.STR_RECORD_PAY.equals(type) && !GlobalDef.STR_RECORD_INCOME.equals(type)
                && !GlobalDef.STR_RECORD_BUDGET.equals(type)))
            return;

        // for ui
        LOG_TAG = "ACPreveiwAndEdit";

        Object ob;
        PayIncomeDBUtility puit = ContextUtil.getPayIncomeUtility();
        BudgetDBUtility buit = ContextUtil.getBudgetUtility();
        int id = it.getIntExtra(GlobalDef.INTENT_LOAD_RECORD_ID, -1);
        if(GlobalDef.STR_RECORD_PAY.equals(type)) {
            PayNoteItem pi = -1 != id ? puit.getPayDBUtility().getData(id) : null;
            ob = pi;
        } else if(GlobalDef.STR_RECORD_BUDGET.equals(type)) {
            BudgetItem bi = -1 != id ? buit.getData(id) : null;
            ob = bi;
        } else  {
            IncomeNoteItem ii = -1 != id ? puit.getIncomeDBUtility().getData(id) : null;
            ob = ii;
        }

        FrgPreviewAndEdit tpe = new FrgPreviewAndEdit();
        tpe.setCurData(type, ob == null ? GlobalDef.STR_CREATE : GlobalDef.STR_MODIFY, ob);
        mTFBase = tpe;
        mFGSupportHolder = tpe;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_preview_edit, menu);

        mMISwitch = menu.findItem(R.id.mi_switch);
        mMISave   = menu.findItem(R.id.mi_save);
        //MenuItem mMIGiveup = menu.findItem(R.id.mi_giveup);
        mMISwitch.setTitle(mTFBase.isPreviewPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
        mMISave.setVisible(mTFBase.isEditPage());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_switch : {
                mMISave.setVisible(mTFBase.isPreviewPage());
                mMISwitch.setTitle(mTFBase.isEditPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
                if(mTFBase.isPreviewPage())
                    mTFBase.toEditPage();
                else
                    mTFBase.toPreviewPage();
            }
            break;

            case R.id.mi_save: {
                if(mTFBase.onAccept())    {
                    int ret_data = GlobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
                    finish();
                }
            }
            break;

            case R.id.mi_giveup:    {
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
