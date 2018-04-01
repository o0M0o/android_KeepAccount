package wxm.KeepAccount.ui.data.edit.Note;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import wxm.KeepAccount.ui.base.SwitcherActivity.ACSwitcherActivity;
import wxm.androidutil.util.UtilFun;
import wxm.KeepAccount.R;
import wxm.KeepAccount.db.BudgetDBUtility;
import wxm.KeepAccount.db.PayIncomeDBUtility;
import wxm.KeepAccount.define.GlobalDef;
import wxm.KeepAccount.utility.ContextUtil;

/**
 * preview/edit UI for note
 */
public class ACPreveiwAndEdit extends ACSwitcherActivity<FrgPreviewAndEdit> {
    private final static String CHANGETO_PREVIEW = "预览";
    private final static String CHANGETO_EDIT = "编辑";
    private MenuItem mMISwitch;
    private MenuItem mMISave;

    @Override
    protected void initUi(Bundle savedInstanceState) {
        super.initUi(savedInstanceState);

        Intent it = getIntent();
        String type = it.getStringExtra(GlobalDef.INTENT_LOAD_RECORD_TYPE);
        if (UtilFun.StringIsNullOrEmpty(type)
                || (!GlobalDef.STR_RECORD_PAY.equals(type) && !GlobalDef.STR_RECORD_INCOME.equals(type)
                && !GlobalDef.STR_RECORD_BUDGET.equals(type)))
            return;

        Object ob = null;
        PayIncomeDBUtility puit = ContextUtil.getPayIncomeUtility();
        BudgetDBUtility buit = ContextUtil.getBudgetUtility();
        int id = it.getIntExtra(GlobalDef.INTENT_LOAD_RECORD_ID, -1);
        if(-1 != id)    {
            switch (type)   {
                case GlobalDef.STR_RECORD_PAY : {
                    ob = puit.getPayDBUtility().getData(id);
                }
                break;

                case GlobalDef.STR_RECORD_INCOME : {
                    ob = puit.getIncomeDBUtility().getData(id);
                }
                break;

                case GlobalDef.STR_RECORD_BUDGET : {
                    ob = buit.getData(id);
                }
                break;
            }
        }

        FrgPreviewAndEdit tpe = new FrgPreviewAndEdit();
        tpe.setCurData(type, ob == null ? GlobalDef.STR_CREATE : GlobalDef.STR_MODIFY, ob);
        addFragment(tpe);
    }

    @Override
    protected void leaveActivity() {
        int ret_data = GlobalDef.INTRET_GIVEUP;

        Intent data = new Intent();
        setResult(ret_data, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mu_preview_edit, menu);

        mMISwitch = menu.findItem(R.id.mi_switch);
        mMISave = menu.findItem(R.id.mi_save);
        mMISwitch.setTitle(getHotFragment().isPreviewPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
        mMISave.setVisible(getHotFragment().isEditPage());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_switch: {
                mMISave.setVisible(getHotFragment().isPreviewPage());
                mMISwitch.setTitle(getHotFragment().isEditPage() ? CHANGETO_EDIT : CHANGETO_PREVIEW);
                if (getHotFragment().isPreviewPage())
                    getHotFragment().toEditPage();
                else
                    getHotFragment().toPreviewPage();
            }
            break;

            case R.id.mi_save: {
                if (getHotFragment().onAccept()) {
                    int ret_data = GlobalDef.INTRET_SURE;
                    Intent data = new Intent();
                    setResult(ret_data, data);
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
